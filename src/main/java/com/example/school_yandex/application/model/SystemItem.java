package com.example.school_yandex.application.model;

import com.example.school_yandex.application.domain.SystemItemType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Модель файла.
 *
 * @author Egor Mitrofanov.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class SystemItem {

    /**
     * Имя.
     */
    private String id;

    /**
     * Ссылка на файл.
     */
    private String url;

    /**
     * Время последнего обновления.
     */
    private LocalDateTime date;

    /**
     * Идентификатор родительского элемента.
     */
    private String parentId;

    /**
     * Тип файла.
     */
    private SystemItemType type;

    /**
     * Размер файла.
     */
    private Long size;

    /**
     * Дочерние элементы.
     */
    private List<SystemItem> children;

}