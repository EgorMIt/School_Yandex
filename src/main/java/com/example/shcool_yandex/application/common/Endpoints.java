package com.example.shcool_yandex.application.common;

/**
 * Список эндпоинтов.
 *
 * @author Egor Mitrofanov.
 */
public interface Endpoints {
    String IMPORT_ITEMS = "/imports";
    String DELETE_ITEM = "/delete/{id}";
    String GET_ITEM = "/nodes/{id}";
    String UPDATES = "/updates";
    String ITEM_HISTORY = "/node/{id}/history";
}
