package com.example.shcool_yandex.application.utils;

import com.example.shcool_yandex.application.domain.SystemItemEntity;
import com.example.shcool_yandex.application.model.SystemItem;
import com.example.shcool_yandex.application.model.SystemItemHistoryUnit;
import org.springframework.stereotype.Component;

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
                entity.getDate(),
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
               entity.getDate());
    }

}
