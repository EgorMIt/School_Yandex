package com.example.school_yandex.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Возвращаемый ответ со списком изменений.
 *
 * @author Egor Mitrofanov.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class SystemItemHistoryResponse {

    /**
     * Список изменений.
     */
    private List<SystemItemHistoryUnit> items;

}
