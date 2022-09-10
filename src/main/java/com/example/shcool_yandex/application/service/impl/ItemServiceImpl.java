package com.example.shcool_yandex.application.service.impl;

import com.example.shcool_yandex.application.domain.SystemItemEntity;
import com.example.shcool_yandex.application.domain.SystemItemRepository;
import com.example.shcool_yandex.application.domain.SystemItemType;
import com.example.shcool_yandex.application.error.ErrorDescriptions;
import com.example.shcool_yandex.application.model.SystemItem;
import com.example.shcool_yandex.application.model.SystemItemHistoryResponse;
import com.example.shcool_yandex.application.model.SystemItemHistoryUnit;
import com.example.shcool_yandex.application.model.SystemItemImport;
import com.example.shcool_yandex.application.service.ItemService;
import com.example.shcool_yandex.application.utils.ModelMapper;
import com.example.shcool_yandex.application.utils.ModelValidator;
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
     * {@link ModelValidator}.
     */
    private final ModelValidator modelValidator;

    /**
     * {@link DateTimeFormatter}.
     */
    private final DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;

    @Override
    @Transactional
    public void createItem(SystemItemImport item, String updateDate) {
        log.info("invoke createItem({}, {})", item, updateDate);
        modelValidator.validateItemImport(item);
        modelValidator.validateDate(updateDate);

        SystemItemEntity entity = new SystemItemEntity();
        SystemItemType type = SystemItemType.valueOf(item.getType());

        entity.setNameId(item.getId());
        entity.setDate(LocalDateTime.parse(updateDate, formatter));
        entity.setType(type);
        entity.setIsActive(true);

        if (!ObjectUtils.isEmpty(item.getParentId())) {
            systemItemRepository.findByNameIdAndIsActive(item.getParentId(), true)
                    .orElseThrow(ErrorDescriptions.ITEM_NOT_FOUND::exception);
            entity.setParentId(item.getParentId());
        } else {
            entity.setParentId(null);
        }

        if (systemItemRepository.findByNameIdAndIsActive(item.getId(), true).isPresent()) {
            SystemItemEntity previousEntity = systemItemRepository.findByNameIdAndIsActive(item.getId(), true).get();
            ErrorDescriptions.INCORRECT_ITEM_REQUEST.throwIfFalse(type.equals(previousEntity.getType()));
            previousEntity.setIsActive(false);
            if (type == SystemItemType.FOLDER) {
                entity.setSize(previousEntity.getSize());
            } else {
                entity.setSize(item.getSize());
            }
            systemItemRepository.save(previousEntity);
            if ((previousEntity.getParentId() != null && (entity.getParentId() == null
                    || !previousEntity.getParentId().equals(entity.getParentId())))) {
                updateParentFolderSize(previousEntity.getParentId(), -previousEntity.getSize());
            }
            if (previousEntity.getParentId() != null) {
                updateParentFolderSize(entity.getParentId(), item.getSize() - previousEntity.getSize());
            } else {
                updateParentFolderSize(entity.getParentId(), item.getSize());
            }

        } else {
            if (type == SystemItemType.FILE) {
                entity.setSize(item.getSize());
                entity.setUrl(item.getUrl());
                updateParentFolderSize(entity.getParentId(), item.getSize());
            }
            else {
                entity.setSize(0L);
                entity.setUrl(null);
            }
        }

        systemItemRepository.save(entity);
    }

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

    @Override
    public void deleteItem(String systemItemNameId) {
        log.info("invoke deleteItem({})", systemItemNameId);
        SystemItemEntity entity = systemItemRepository.findByNameIdAndIsActive(systemItemNameId, true)
                .orElseThrow(ErrorDescriptions.ITEM_NOT_FOUND::exception);
        List<SystemItemEntity> children = systemItemRepository.findSystemItemEntitiesByParentIdAndIsActive(entity.getNameId(), true);
        if (children.isEmpty()) {
            systemItemRepository.delete(entity);
        } else {
            children.forEach((item) -> deleteItem(item.getNameId()));
            systemItemRepository.delete(entity);
        }
    }

    @Override
    public SystemItemHistoryResponse getUpdates(String date) {
        log.info("invoke getUpdates({})", date);
        modelValidator.validateDate(date);

        LocalDateTime finishDate = LocalDateTime.parse(date, formatter);
        LocalDateTime startDate = finishDate.minusDays(1);
        List<SystemItemHistoryUnit> items = systemItemRepository.findAllByIsActiveAndTypeAndDateBetweenOrderByDateDesc(true, SystemItemType.FILE, startDate, finishDate)
                .stream()
                .map(modelMapper::mapToSystemItemHistoryUnit)
                .collect(Collectors.toList());
        return SystemItemHistoryResponse.of(items);
    }

    @Override
    public SystemItemHistoryResponse getHistoryForItem(String systemItemNameId, String dateStart, String dateEnd) {
        log.info("invoke getHistoryForItem({}, {}, {})", systemItemNameId, dateStart, dateEnd);
        modelValidator.validateDate(dateStart);
        modelValidator.validateDate(dateEnd);

        LocalDateTime startDate = LocalDateTime.parse(dateStart, formatter);
        LocalDateTime finishDate = LocalDateTime.parse(dateEnd, formatter);
        finishDate = finishDate.minusSeconds(1);

        List<SystemItemHistoryUnit> items = systemItemRepository.findAllByNameIdAndDateBetweenOrderByDateDesc(systemItemNameId, startDate, finishDate)
                .stream()
                .map(modelMapper::mapToSystemItemHistoryUnit)
                .collect(Collectors.toList());
        return SystemItemHistoryResponse.of(items);
    }

    private void updateParentFolderSize(String systemItemNameId, Long size) {
        if (!ObjectUtils.isEmpty(systemItemNameId)) {
            SystemItemEntity entity = systemItemRepository.findByNameIdAndIsActive(systemItemNameId, true)
                    .orElseThrow(ErrorDescriptions.ITEM_NOT_FOUND::exception);
            long oldSize = entity.getSize() == null ? 0 : entity.getSize();
            entity.setSize(oldSize + size);
            systemItemRepository.save(entity);
            updateParentFolderSize(entity.getParentId(), size);
        }
    }
}
