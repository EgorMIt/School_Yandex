package com.example.shcool_yandex.application.web;

import com.example.shcool_yandex.application.common.Endpoints;
import com.example.shcool_yandex.application.model.SystemItem;
import com.example.shcool_yandex.application.model.SystemItemImportRequest;
import com.example.shcool_yandex.application.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Обработчик запросов к {@link ItemService}.
 *
 * @author Egor Mitrofanov.
 */
@RestController
@RequiredArgsConstructor
public class ItemController {

    /**
     * {@link ItemService}.
     */
    private final ItemService itemService;

    /**
     * Импорт новых объектов system_item.
     *
     * @param request запрос на импорт.
     */
    @PostMapping(value = Endpoints.IMPORT_ITEMS)
    public ResponseEntity<Object> importItems(@RequestBody SystemItemImportRequest request) {
        request.getItems().forEach((it) -> itemService.createItem(it, request.getUpdateDate()));
        return ResponseEntity.ok().build();
    }

    /**
     * Получение объекта system_item.
     *
     * @param id идентификатор объекта.
     * @return объект system_item.
     */
    @GetMapping(value = Endpoints.GET_ITEM)
    public ResponseEntity<SystemItem> getItem(@PathVariable String id) {
        return new ResponseEntity<>(itemService.getItem(id), HttpStatus.OK);
    }

    /**
     * Удаление объекта system_item.
     *
     * @param id идентификатор объекта.
     */
    @DeleteMapping(value = Endpoints.DELETE_ITEM)
    public ResponseEntity<Object> deleteItem(@PathVariable String id) {
        return ResponseEntity.ok().build();
    }
}
