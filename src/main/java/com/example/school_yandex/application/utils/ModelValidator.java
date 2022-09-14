package com.example.school_yandex.application.utils;

import com.example.school_yandex.application.domain.SystemItemType;
import com.example.school_yandex.application.dto.SystemItemImport;
import com.example.school_yandex.application.dto.SystemItemImportRequest;
import com.example.school_yandex.application.error.ErrorDescriptions;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.HashSet;
import java.util.List;

/**
 * Валидатор моделей.
 *
 * @author Egor Mitrofanov.
 */
@Component
public class ModelValidator {

    /**
     * Валидация входной модели {@link SystemItemImport}.
     *
     * @param item модель.
     */
    public void validateItemImport(SystemItemImport item) {
        ErrorDescriptions.INCORRECT_ITEM_REQUEST.throwIfTrue(ObjectUtils.isEmpty(item.getId()));
        ErrorDescriptions.INCORRECT_ITEM_REQUEST.throwIfTrue(ObjectUtils.isEmpty(item.getType()));

        try {
            SystemItemType.valueOf((item.getType()).toUpperCase().trim());
        } catch (Exception e) {
            ErrorDescriptions.INCORRECT_ITEM_REQUEST.throwException();
        }

        switch (SystemItemType.valueOf(item.getType())) {
            case FILE: {
                if (!ObjectUtils.isEmpty(item.getUrl()))
                    ErrorDescriptions.INCORRECT_ITEM_REQUEST.throwIfTrue(item.getUrl().length() > 255);
                if (!ObjectUtils.isEmpty(item.getSize()))
                    ErrorDescriptions.INCORRECT_ITEM_REQUEST.throwIfFalse(item.getSize() > 0);
                break;
            }
            case FOLDER: {
                ErrorDescriptions.INCORRECT_ITEM_REQUEST.throwIfTrue(item.getUrl() != null);
                ErrorDescriptions.INCORRECT_ITEM_REQUEST.throwIfTrue(item.getSize() != null);
                break;
            }
        }
    }

    /**
     * Валидация даты на соответствие формату ISO 8601.
     *
     * @param date дата.
     */
    public void validateDate(String date) {
        ErrorDescriptions.INCORRECT_ITEM_REQUEST.throwIfTrue(ObjectUtils.isEmpty(date));
        String regEx = "^(-?(?:[1-9][0-9]*)?[0-9]{4})-(1[0-2]|0[1-9])-(3[01]|0[1-9]|[12][0-9])T(2[0-3]|[01][0-9]):([0-5][0-9]):([0-5][0-9])(.[0-9]+)?(Z)?$";
        ErrorDescriptions.INCORRECT_ITEM_REQUEST.throwIfFalse(date.matches(regEx));
    }

    /**
     * Валидация import запроса на неповторение id элементов.
     *
     * @param request запрос.
     */
    public void validateImportNames(SystemItemImportRequest request) {
        List<SystemItemImport> items = request.getItems();
        HashSet<String> itemIds = new HashSet<>();
        items.forEach((item) -> {
            if (!itemIds.contains(item.getId())) {
                itemIds.add(item.getId());
            } else {
                ErrorDescriptions.INCORRECT_ITEM_REQUEST.throwException();
            }
        });
        itemIds.clear();
    }
}
