package com.example.school_yandex.application.domain;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.time.ZonedDateTime;

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
    private ZonedDateTime date;

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

    /**
     * Флаг актуальной версии файла.
     */
    @Column(name = "is_active")
    private Boolean isActive;

}
