package com.example.shcool_yandex.application.model;

import com.example.shcool_yandex.application.domain.SystemItemType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Модель файла для импорта.
 *
 * @author Egor Mitrofanov.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class SystemItemImport {

    /**
     * Имя.
     */
    private String id;

    /**
     * Ссылка на файл.
     */
    private String url;

    /**
     * Идентификатор родительского элемента.
     */
    private String parentId;

    /**
     * Тип файла.
     */
    private String type;

    /**
     * Размер файла.
     */
    private Long size;

}
