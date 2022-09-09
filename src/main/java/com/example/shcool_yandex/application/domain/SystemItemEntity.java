package com.example.shcool_yandex.application.domain;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Сущность файла.
 *
 * @author Egor Mitrofanov.
 */
@Data
@Entity
@Table(name = "system_item")
public class SystemItemEntity {

    /**
     * Идентификатор.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequence")
    @SequenceGenerator(name = "sequence", allocationSize = 1, sequenceName = "system_item_seq")
    private Long id;

    /**
     * Имя.
     */
    @Column(name = "item_name")
    private String nameId;

    /**
     * Ссылка на файл.
     */
    @Column(name = "url")
    private String url;

    /**
     * Время последнего обновления.
     */
    @Column(name = "date")
    private LocalDateTime date;

    /**
     * Идентификатор родительского элемента.
     */
    @Column(name = "parent_id")
    private String parentId;

    /**
     * Тип файла.
     */
    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private SystemItemType type;

    /**
     * Размер файла.
     */
    @Column(name = "size")
    private Long size;

}
