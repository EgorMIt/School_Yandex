package com.example.shcool_yandex.application.service.impl;

import com.example.shcool_yandex.application.domain.*;
import com.example.shcool_yandex.application.error.ErrorDescriptions;
import com.example.shcool_yandex.application.model.SystemItem;
import com.example.shcool_yandex.application.model.SystemItemImport;
import com.example.shcool_yandex.application.service.ItemService;
import com.example.shcool_yandex.application.utils.ModelMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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

    @Override
    public void createItem(SystemItemImport systemItemImport, String updateDate) {
        log.info("invoke createItem({}, {})", systemItemImport, updateDate);
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
        SystemItemEntity entity;
        if (systemItemRepository.findByNameId(systemItemImport.getId()).isPresent()) {
            entity = systemItemRepository.findByNameId(systemItemImport.getId()).get();
        } else {
            entity = new SystemItemEntity();
        }
        entity.setNameId(systemItemImport.getId());
        entity.setDate(LocalDateTime.parse(updateDate, formatter));
        entity.setType(SystemItemType.valueOf(systemItemImport.getType()));
        entity.setUrl(systemItemImport.getUrl());
        entity.setParentId(systemItemImport.getParentId());
        entity.setSize(systemItemImport.getSize());

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
        systemItemRepository.delete(entity);
    }
}
