package com.example.school_yandex.application.error;

import com.example.school_yandex.application.error.model.ApplicationError;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * Ошибки с комментариями.
 *
 * @author Egor Mitrofanov.
 */
@Getter
@RequiredArgsConstructor
public enum ErrorDescriptions {
    INCORRECT_ITEM_REQUEST("Validation Failed", HttpStatus.BAD_REQUEST.value()),
    ITEM_NOT_FOUND("Item not found", HttpStatus.NOT_FOUND.value()),
    HANDLER_NOT_FOUND("HANDLER_NOT_FOUND", HttpStatus.NOT_FOUND.value());

    /**
     * Сообщение ошибки.
     */
    private final String message;

    /**
     * Код ошибки.
     */
    private final Integer code;

    /**
     * Метод выбрасывает исключение приложения.
     *
     * @throws ApplicationException исключение приложения
     */
    public void throwException() throws ApplicationException {
        throw exception();
    }

    /**
     * Метод выбрасывает исключение если объект == null.
     *
     * @param obj объект для проверки
     */
    public void throwIfNull(Object obj) {
        if (obj == null) {
            throw exception();
        }
    }

    /**
     * Метод выбрасывает ислючение если условие истинно.
     *
     * @param condition условие для проверки
     */
    public void throwIfTrue(Boolean condition) {
        if (condition) {
            throw exception();
        }
    }

    /**
     * Метод выбрасывает ислючение если условие ложно.
     *
     * @param condition условие для проверки
     */
    public void throwIfFalse(Boolean condition) {
        if (!condition) {
            throw exception();
        }
    }

    public ApplicationError applicationError() {
        return new ApplicationError(this.message, this.code);
    }


    public ApplicationException exception() {
        return new ApplicationException(applicationError());
    }
}
