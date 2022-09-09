package com.example.shcool_yandex.application.service.impl;

import com.example.shcool_yandex.application.domain.*;
import com.example.shcool_yandex.application.error.ErrorDescriptions;
import com.example.shcool_yandex.application.model.SystemItem;
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
     * {@link SystemItemHistoryRepository}.
     */
    private final SystemItemHistoryRepository systemItemHistoryRepository;

    /**
     * {@link ModelMapper}.
     */
    private final ModelMapper modelMapper;

    /**
     * {@link ModelValidator}.
     */
    private final ModelValidator modelValidator;

    @Override
    @Transactional
    public void createItem(SystemItemImport item, String updateDate) {
        log.info("invoke createItem({}, {})", item, updateDate);
        modelValidator.validateItemImport(item);
        modelValidator.validateDate(updateDate);

        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;

        SystemItemEntity entity;
        if (systemItemRepository.findByNameId(item.getId()).isPresent()) {
            entity = systemItemRepository.findByNameId(item.getId()).get();
        } else {
            entity = new SystemItemEntity();
        }
        SystemItemType type = SystemItemType.valueOf(item.getType());
        entity.setNameId(item.getId());
        entity.setDate(LocalDateTime.parse(updateDate, formatter));
        entity.setType(type);
        entity.setUrl(item.getUrl());

        if (!ObjectUtils.isEmpty(item.getParentId())) {
            SystemItemEntity parentEntity = systemItemRepository.findByNameId(item.getParentId())
                    .orElseThrow(ErrorDescriptions.ITEM_NOT_FOUND::exception);
            entity.setParentId(item.getParentId());
        } else {
            entity.setParentId(null);
        }
        if (type == SystemItemType.FILE) {
            entity.setSize(item.getSize());
            updateParentFolderSize(entity.getParentId(), item.getSize());
        } else {
            entity.setSize(null);
        }

        systemItemRepository.save(entity);


        SystemItemHistoryEntity historyEntity = new SystemItemHistoryEntity();
        historyEntity.setSystemItem(entity);
        historyEntity.setDate(LocalDateTime.parse(updateDate, formatter));
        systemItemHistoryRepository.save(historyEntity);
    }

    @Override
    public SystemItem getItem(String systemItemNameId) {
        log.info("invoke getItem({})", systemItemNameId);
        SystemItemEntity entity = systemItemRepository.findByNameId(systemItemNameId)
                .orElseThrow(ErrorDescriptions.ITEM_NOT_FOUND::exception);
        List<SystemItemEntity> children = systemItemRepository.findSystemItemEntitiesByParentId(entity.getNameId());
        if (children.isEmpty()) {
            return modelMapper.mapToSystemItem(entity, entity.getType() == SystemItemType.FILE ? null : Collections.emptyList());
        } else {
            List<SystemItem> systemItems = new ArrayList<>();
            children.forEach((item) -> systemItems.add(getItem(item.getNameId())));
            return modelMapper.mapToSystemItem(entity, systemItems);
        }
    }

    @Override
    public void deleteItem(String systemItemNameId) {
        log.info("invoke deleteItem({})", systemItemNameId);
        SystemItemEntity entity = systemItemRepository.findByNameId(systemItemNameId)
                .orElseThrow(ErrorDescriptions.ITEM_NOT_FOUND::exception);
        List<SystemItemEntity> children = systemItemRepository.findSystemItemEntitiesByParentId(entity.getNameId());
        if (children.isEmpty()) {
            systemItemRepository.delete(entity);
        } else {
            children.forEach((item) -> deleteItem(item.getNameId()));
            systemItemRepository.delete(entity);
        }
    }

    private void updateParentFolderSize(String systemItemNameId, Long size) {
        if (!ObjectUtils.isEmpty(systemItemNameId)) {
            SystemItemEntity entity = systemItemRepository.findByNameId(systemItemNameId)
                    .orElseThrow(ErrorDescriptions.ITEM_NOT_FOUND::exception);
            long oldSize = entity.getSize() == null ? 0 : entity.getSize();
            entity.setSize(oldSize + size);
            systemItemRepository.save(entity);
            updateParentFolderSize(entity.getParentId(), size);
        }
    }
}
