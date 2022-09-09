package com.example.shcool_yandex.application.domain;

import org.springframework.data.jpa.repository.JpaRepository;

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
     * @param nameId идентификатор
     * @return {@link SystemItemEntity}
     */
    Optional<SystemItemEntity> findByNameId(String nameId);

    /**
     * Поиск system_item по paren_id.
     *
     * @param parentId идентификатор
     * @return список {@link SystemItemEntity}
     */
    List<SystemItemEntity> findSystemItemEntitiesByParentId(String parentId);
}
