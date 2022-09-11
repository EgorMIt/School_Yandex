package com.example.shcool_yandex.application.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Репозиторий доступа к сущности {@link SystemItemEntity}.
 *
 * @author Egor Mitrofanov.
 */
public interface SystemItemRepository extends JpaRepository<SystemItemEntity, Long> {

    /**
     * Поиск system_item по ID.
     *
     * @param nameId   идентификатор
     * @param isActive флаг актуальной версии
     * @return {@link SystemItemEntity}
     */
    Optional<SystemItemEntity> findByNameIdAndIsActive(String nameId, Boolean isActive);

    /**
     * Поиск system_item по paren_id.
     *
     * @param parentId идентификатор
     * @param isActive флаг актуальной версии
     * @return список {@link SystemItemEntity}
     */
    List<SystemItemEntity> findSystemItemEntitiesByParentIdAndIsActive(String parentId, Boolean isActive);

    /**
     * Поиск всех актуальных system_item в промежутке дат.
     *
     * @param isActive   флаг актуальной версии
     * @param startDate  дата начала
     * @param finishDate дата окончания
     * @return список {@link SystemItemEntity}
     */
    List<SystemItemEntity> findAllByIsActiveAndTypeAndDateBetweenOrderByDateDesc(Boolean isActive, SystemItemType type, LocalDateTime startDate, LocalDateTime finishDate);

    /**
     * Получение всех версий объекта system_item в промежутке дат.
     *
     * @param nameId     идентификатор
     * @param startDate  дата начала
     * @param finishDate дата окончания
     * @return список {@link SystemItemEntity}
     */
    List<SystemItemEntity> findAllByNameIdAndDateBetweenOrderByDateDesc(String nameId, LocalDateTime startDate, LocalDateTime finishDate);

}
