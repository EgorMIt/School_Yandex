package com.example.shcool_yandex.application.service;

import com.example.shcool_yandex.application.model.SystemItem;
import com.example.shcool_yandex.application.model.SystemItemImport;

/**
 * Интерфейс сервиса работы с system_item.
 *
 * @author Egor Mitrofanov.
 */
public interface ItemService {

    /**
     * Создание нового файла.
     *
     * @param systemItemImport модель.
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

}
