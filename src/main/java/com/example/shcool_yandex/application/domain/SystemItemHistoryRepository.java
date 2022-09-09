package com.example.shcool_yandex.application.domain;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Репозиторий доступа к сущности {@link SystemItemHistoryEntity}.
 *
 * @author Egor Mitrofanov.
 */
public interface SystemItemHistoryRepository extends JpaRepository<SystemItemHistoryEntity, Long> {

}
