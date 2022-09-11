package com.example.school_yandex.application.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Модель импорта файлов.
 *
 * @author Egor Mitrofanov.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class SystemItemImportRequest {

    /**
     * Список импортируемых элементов.
     */
    private List<SystemItemImport> items;

    /**
     * Дата вставки.
     */
    private String updateDate;

}
