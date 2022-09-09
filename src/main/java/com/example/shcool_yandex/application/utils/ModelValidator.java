package com.example.shcool_yandex.application.utils;

import com.example.shcool_yandex.application.domain.SystemItemType;
import com.example.shcool_yandex.application.error.ErrorDescriptions;
import com.example.shcool_yandex.application.model.SystemItemImport;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

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
        ErrorDescriptions.INCORRECT_ITEM_REQUEST.throwIfTrue(item.getUrl().length() > 255);
        ErrorDescriptions.INCORRECT_ITEM_REQUEST.throwIfFalse(item.getSize() > 0);
        ErrorDescriptions.INCORRECT_ITEM_REQUEST.throwIfTrue(ObjectUtils.isEmpty(item.getId()));
        ErrorDescriptions.INCORRECT_ITEM_REQUEST.throwIfTrue(ObjectUtils.isEmpty(item.getType()));

        try {
            SystemItemType.valueOf((item.getType()).toUpperCase().trim());
        } catch (Exception e) {
            ErrorDescriptions.INCORRECT_ITEM_REQUEST.throwException();
        }
    }

    /**
     * Валидация даты на соответствие формату ISO 8601.
     *
     * @param date дата.
     */
    public void validateDate(String date) {
        String regEx = "^(-?(?:[1-9][0-9]*)?[0-9]{4})-(1[0-2]|0[1-9])-(3[01]|0[1-9]|[12][0-9])T(2[0-3]|[01][0-9]):([0-5][0-9]):([0-5][0-9])(.[0-9]+)?(Z)?$";
        ErrorDescriptions.INCORRECT_ITEM_REQUEST.throwIfFalse(date.matches(regEx));
    }
}
