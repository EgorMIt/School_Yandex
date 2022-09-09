package com.example.shcool_yandex.application.domain;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Сущность записи об обновлении файла.
 *
 * @author Egor Mitrofanov.
 */
@Data
@Entity
@Table(name = "system_item_history")
public class SystemItemHistoryEntity {

    /**
     * Идентификатор.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequence")
    @SequenceGenerator(name = "sequence", allocationSize = 1, sequenceName = "system_item_history_seq")
    private Long id;

    /**
     * Идентификатор файла.
     */
    @ManyToOne
    @JoinColumn(name = "item_id")
    private SystemItemEntity systemItem;

    /**
     * Время обновления.
     */
    @Column(name = "date")
    private LocalDateTime date;

}
