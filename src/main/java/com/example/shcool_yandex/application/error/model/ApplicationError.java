package com.example.shcool_yandex.application.error.model;

import lombok.Data;
import org.springframework.http.HttpStatus;

/**
 * Ошибка приложения.
 *
 * @author Egor Mitrofanov.
 */
@Data
public class ApplicationError {

    /**
     * Сообщение об ошибке.
     */
    private String message;

    /**
     * Статус, который возвращается при вызове ошибки.
     */
    private HttpStatus status;

    /**
     * Создание ошибки.
     *
     * @param message текст ошибки.
     * @param status  статус ошибки.
     */
    public ApplicationError(String message, HttpStatus status) {
        this.message = message;
        this.status = status;
    }
}
