package com.example.school_yandex.application.service.impl;

import com.example.school_yandex.application.domain.SystemItemEntity;
import com.example.school_yandex.application.domain.SystemItemRepository;
import com.example.school_yandex.application.domain.SystemItemType;
import com.example.school_yandex.application.dto.SystemItem;
import com.example.school_yandex.application.dto.SystemItemHistoryResponse;
import com.example.school_yandex.application.dto.SystemItemHistoryUnit;
import com.example.school_yandex.application.dto.SystemItemImport;
import com.example.school_yandex.application.error.ErrorDescriptions;
import com.example.school_yandex.application.service.ItemService;
import com.example.school_yandex.application.utils.ModelMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Реализация сервиса работы с system_item.
 *
 * @author Egor Mitrofanov.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    /**
     * {@link SystemItemRepository}.
     */
    private final SystemItemRepository systemItemRepository;

    /**
     * {@link ModelMapper}.
     */
    private final ModelMapper modelMapper;

    /**
     * {@link DateTimeFormatter}.
     */
    private final DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;

    /**
     * Создание нового элемента.
     *
     * @param item       модель.
     * @param updateDate дата импорта.
     */
    @Override
    @Transactional
    public void createItem(SystemItemImport item, String updateDate) {
        log.info("invoke createItem({}, {})", item, updateDate);

        SystemItemEntity entity = new SystemItemEntity();
        SystemItemType type = SystemItemType.valueOf(item.getType());
        LocalDateTime date = LocalDateTime.parse(updateDate, formatter);

        entity.setNameId(item.getId());
        entity.setDate(date);
        entity.setType(type);
        entity.setIsActive(true);

        if (!ObjectUtils.isEmpty(item.getParentId())) {
            systemItemRepository.findByNameIdAndIsActive(item.getParentId(), true)
                    .orElseThrow(ErrorDescriptions.ITEM_NOT_FOUND::exception);
            entity.setParentId(item.getParentId());
        } else {
            entity.setParentId(null);
        }

        //Проверка, нужно ли обновлять элемент, или создавать новый
        if (systemItemRepository.findByNameIdAndIsActive(item.getId(), true).isPresent()) {
            SystemItemEntity previousEntity = systemItemRepository.findByNameIdAndIsActive(item.getId(), true).get();

            //Выключение предыдущей версии элемента
            ErrorDescriptions.INCORRECT_ITEM_REQUEST.throwIfFalse(type.equals(previousEntity.getType()));
            previousEntity.setIsActive(false);
            systemItemRepository.save(previousEntity);

            switch (type) {
                case FILE: {
                    entity.setSize(previousEntity.getSize());
                    break;
                }
                case FOLDER: {
                    entity.setSize(item.getSize());
                    break;
                }
            }
            //Обновление размера предыдущей папки при перемещении в другую
            if ((previousEntity.getParentId() != null && (entity.getParentId() == null
                    || !previousEntity.getParentId().equals(entity.getParentId())))) {
                updateParentFolder(previousEntity.getParentId(), -previousEntity.getSize(), date);
            }
            //Обновление размера родительской папки
            if (previousEntity.getParentId() != null) {
                updateParentFolder(entity.getParentId(), item.getSize() - previousEntity.getSize(), date);
            } else {
                updateParentFolder(entity.getParentId(), item.getSize(), date);
            }

        } else {
            switch (type) {
                case FILE: {
                    entity.setSize(item.getSize());
                    entity.setUrl(item.getUrl());
                    updateParentFolder(entity.getParentId(), item.getSize(), date);
                    break;
                }
                case FOLDER: {
                    entity.setSize(0L);
                    entity.setUrl(null);
                    break;
                }
            }
        }

        systemItemRepository.save(entity);
    }

    /**
     * Получение объекта system_item.
     *
     * @param systemItemNameId идентификатор system_item.
     * @return модель объекта.
     */
    @Override
    public SystemItem getItem(String systemItemNameId) {
        log.info("invoke getItem({})", systemItemNameId);
        SystemItemEntity entity = systemItemRepository.findByNameIdAndIsActive(systemItemNameId, true)
                .orElseThrow(ErrorDescriptions.ITEM_NOT_FOUND::exception);
        List<SystemItemEntity> children = systemItemRepository.findSystemItemEntitiesByParentIdAndIsActive(entity.getNameId(), true);
        if (children.isEmpty()) {
            return modelMapper.mapToSystemItem(entity, entity.getType() == SystemItemType.FILE ? null : Collections.emptyList());
        } else {
            List<SystemItem> childrenSystemItems = new ArrayList<>();
            children.forEach((item) -> childrenSystemItems.add(getItem(item.getNameId())));
            return modelMapper.mapToSystemItem(entity, childrenSystemItems);
        }
    }

    /**
     * Удаление объекта system_item.
     *
     * @param systemItemNameId идентификатор system_item.
     */
    @Override
    public void deleteItem(String systemItemNameId) {
        log.info("invoke deleteItem({})", systemItemNameId);
        SystemItemEntity entity = systemItemRepository.findByNameIdAndIsActive(systemItemNameId, true)
                .orElseThrow(ErrorDescriptions.ITEM_NOT_FOUND::exception);

        //Удаление всех дочерних элементов
        List<SystemItemEntity> children = systemItemRepository.findSystemItemEntitiesByParentIdAndIsActive(entity.getNameId(), true);
        if (children.isEmpty()) {
            systemItemRepository.delete(entity);
        } else {
            children.forEach((item) -> deleteItem(item.getNameId()));
            systemItemRepository.delete(entity);
        }
    }

    /**
     * Получение списка обновленных объектов system_item.
     *
     * @param date дата фильтрации.
     * @return {@link SystemItemHistoryResponse}.
     */
    @Override
    public SystemItemHistoryResponse getUpdates(String date) {
        log.info("invoke getUpdates({})", date);

        LocalDateTime finishDate = LocalDateTime.parse(date, formatter);
        LocalDateTime startDate = finishDate.minusDays(1);

        List<SystemItemHistoryUnit> items = systemItemRepository.findAllByIsActiveAndTypeAndDateBetweenOrderByDateDesc(true, SystemItemType.FILE, startDate, finishDate)
                .stream()
                .map(modelMapper::mapToSystemItemHistoryUnit)
                .collect(Collectors.toList());
        return SystemItemHistoryResponse.of(items);
    }

    /**
     * Получение истории версий для объекта system_item.
     *
     * @param systemItemNameId идентификатор system_item.
     * @param dateStart        дата начала фильтрации.
     * @param dateEnd          дата окончания фильтрации.
     * @return {@link SystemItemHistoryResponse}.
     */
    @Override
    public SystemItemHistoryResponse getHistoryForItem(String systemItemNameId, String dateStart, String dateEnd) {
        log.info("invoke getHistoryForItem({}, {}, {})", systemItemNameId, dateStart, dateEnd);

        LocalDateTime startDate = LocalDateTime.parse(dateStart, formatter);
        LocalDateTime finishDate = LocalDateTime.parse(dateEnd, formatter);
        finishDate = finishDate.minusSeconds(1); // [from; to)

        List<SystemItemHistoryUnit> items = systemItemRepository.findAllByNameIdAndDateBetweenOrderByDateDesc(systemItemNameId, startDate, finishDate)
                .stream()
                .map(modelMapper::mapToSystemItemHistoryUnit)
                .collect(Collectors.toList());
        return SystemItemHistoryResponse.of(items);
    }

    /**
     * Рекурсивное обновление всех родительских элементов.
     *
     * @param systemItemNameId идентификатор родительского system_item.
     * @param size             размер нового файла.
     * @param updateDate       дата обновления.
     */
    private void updateParentFolder(String systemItemNameId, Long size, LocalDateTime updateDate) {
        if (!ObjectUtils.isEmpty(systemItemNameId)) {
            log.info("invoke updateParentFolder({}, {}, {})", systemItemNameId, size, updateDate);

            SystemItemEntity entity = systemItemRepository.findByNameIdAndIsActive(systemItemNameId, true)
                    .orElseThrow(ErrorDescriptions.ITEM_NOT_FOUND::exception);
            SystemItemEntity newEntity = new SystemItemEntity();
            long oldSize = entity.getSize() == null ? 0 : entity.getSize();
            entity.setIsActive(false);

            newEntity.setNameId(entity.getNameId());
            newEntity.setUrl(entity.getUrl());
            newEntity.setType(entity.getType());
            newEntity.setParentId(entity.getParentId());
            newEntity.setIsActive(true);
            newEntity.setSize(oldSize + size);
            newEntity.setDate(updateDate);

            systemItemRepository.save(entity);
            systemItemRepository.save(newEntity);
            updateParentFolder(entity.getParentId(), size, updateDate);
        }
    }
}
