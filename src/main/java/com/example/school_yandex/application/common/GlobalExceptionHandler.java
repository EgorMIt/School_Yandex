package com.example.school_yandex.application.common;

import com.example.school_yandex.application.error.ApplicationException;
import com.example.school_yandex.application.error.ErrorDescriptions;
import com.example.school_yandex.application.error.model.ApplicationError;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletResponse;

/**
 * {@link ControllerAdvice}, обрабатывающий возникающие исключения.
 *
 * @author Egor Mitrofanov.
 */
@Slf4j
@ControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    /**
     * Обработка исключения {@link NoHandlerFoundException}.
     *
     * @param ex исключение.
     * @return ошибка приложения.
     */
    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NoHandlerFoundException.class)
    public ApplicationError handleError404(NoHandlerFoundException ex) {
        return ErrorDescriptions.HANDLER_NOT_FOUND.applicationError();
    }

    /**
     * Обработка бизнес исключений.
     *
     * @param ex исключение.
     * @return ошибка приложения.
     */
    @ResponseBody
    @ExceptionHandler(ApplicationException.class)
    public ApplicationError handleApplicationException(ApplicationException ex, HttpServletResponse response) {
        response.setStatus(ex.getError().getCode());
        log.info("Exception happened {}", ex.getMessage());
        return ex.getError();
    }

}
