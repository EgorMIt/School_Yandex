package com.example.school_yandex.application.utils;

import com.example.school_yandex.application.domain.SystemItemEntity;
import com.example.school_yandex.application.dto.SystemItem;
import com.example.school_yandex.application.dto.SystemItemHistoryUnit;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Маппер моделей.
 *
 * @author Egor Mitrofanov.
 */
@Component
public class ModelMapper {

    /**
     * Маппер сущности {@link SystemItemEntity} в модель {@link SystemItem}.
     *
     * @param entity сущность.
     * @return модель {@link SystemItem}.
     */
    public SystemItem mapToSystemItem(SystemItemEntity entity, List<SystemItem> children) {
        return SystemItem.of(entity.getNameId(),
                entity.getUrl(),
                ZonedDateTime.parse(entity.getDate().format(DateTimeFormatter.ISO_INSTANT)),
                entity.getParentId(),
                entity.getType(),
                entity.getSize(),
                children);
    }

    /**
     * Маппер сущности {@link SystemItemEntity} в модель {@link SystemItemHistoryUnit}.
     *
     * @param entity сущность.
     * @return модель {@link SystemItemHistoryUnit}.
     */
    public SystemItemHistoryUnit mapToSystemItemHistoryUnit(SystemItemEntity entity) {
       return SystemItemHistoryUnit.of(entity.getNameId(),
               entity.getUrl(),
               entity.getParentId(),
               entity.getType(),
               entity.getSize(),
               ZonedDateTime.parse(entity.getDate().format(DateTimeFormatter.ISO_INSTANT)));
    }

}
