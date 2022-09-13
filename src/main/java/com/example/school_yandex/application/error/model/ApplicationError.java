package com.example.school_yandex.application.error.model;

import lombok.Data;

/**
 * Ошибка приложения.
 *
 * @author Egor Mitrofanov.
 */
@Data
public class ApplicationError {

    /**
     * Статус, который возвращается при вызове ошибки.
     */
    private Integer code;

    /**
     * Сообщение об ошибке.
     */
    private String message;

    /**
     * Создание ошибки.
     *
     * @param message текст ошибки.
     * @param code    статус ошибки.
     */
    public ApplicationError(String message, Integer code) {
        this.code = code;
        this.message = message;
    }

}
