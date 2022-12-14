package com.example.school_yandex.application.service;

import com.example.school_yandex.application.dto.SystemItem;
import com.example.school_yandex.application.dto.SystemItemHistoryResponse;
import com.example.school_yandex.application.dto.SystemItemImport;

/**
 * Интерфейс сервиса работы с system_item.
 *
 * @author Egor Mitrofanov.
 */
public interface ItemService {

    /**
     * Создание нового элемента.
     *
     * @param systemItemImport модель.
     * @param updateDate       дата импорта.
     */
    void createItem(SystemItemImport systemItemImport, String updateDate);

    /**
     * Получение объекта system_item.
     *
     * @param systemItemNameId идентификатор system_item.
     * @return модель объекта.
     */
    SystemItem getItem(String systemItemNameId);

    /**
     * Удаление объекта system_item.
     *
     * @param systemItemNameId идентификатор system_item.
     */
    void deleteItem(String systemItemNameId);

    /**
     * Получение списка обновленных объектов system_item.
     *
     * @param date дата фильтрации.
     * @return {@link SystemItemHistoryResponse}.
     */
    SystemItemHistoryResponse getUpdates(String date);

    /**
     * Получение истории версий для объекта system_item.
     *
     * @param systemItemNameId идентификатор system_item.
     * @param dateStart        дата начала фильтрации.
     * @param dateEnd          дата окончания фильтрации.
     * @return {@link SystemItemHistoryResponse}.
     */
    SystemItemHistoryResponse getHistoryForItem(String systemItemNameId, String dateStart, String dateEnd);

}
