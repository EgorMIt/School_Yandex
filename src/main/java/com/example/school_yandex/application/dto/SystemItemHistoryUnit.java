package com.example.school_yandex.application.dto;

import com.example.school_yandex.application.domain.SystemItemType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Модель записи об обновлении файла.
 *
 * @author Egor Mitrofanov.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class SystemItemHistoryUnit {

    /**
     * Имя.
     */
    private String itemId;

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
    private SystemItemType type;

    /**
     * Размер файла.
     */
    private Long size;

    /**
     * Дата обновления.
     */
    private LocalDateTime date;

}
