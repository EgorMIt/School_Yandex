package com.example.school_yandex.application.web;

import com.example.school_yandex.application.common.Endpoints;
import com.example.school_yandex.application.dto.SystemItem;
import com.example.school_yandex.application.dto.SystemItemHistoryResponse;
import com.example.school_yandex.application.dto.SystemItemImportRequest;
import com.example.school_yandex.application.service.ItemService;
import com.example.school_yandex.application.utils.ModelValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
     * {@link ModelValidator}.
     */
    private final ModelValidator modelValidator;

    /**
     * Импорт новых объектов system_item.
     *
     * @param request запрос на импорт.
     */
    @PostMapping(value = Endpoints.IMPORT_ITEMS)
    public ResponseEntity<Object> importItems(@RequestBody SystemItemImportRequest request) {
        modelValidator.validateImportNames(request);
        request.getItems().forEach((it) -> {
            modelValidator.validateItemImport(it);
            modelValidator.validateDate(request.getUpdateDate());
            itemService.createItem(it, request.getUpdateDate());
        });
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
        itemService.deleteItem(id);
        return ResponseEntity.ok().build();
    }

    /**
     * Получение списка обновленных объектов system_item.
     *
     * @param date дата фильтрации.
     * @return {@link SystemItemHistoryResponse}.
     */
    @GetMapping(value = Endpoints.UPDATES)
    public ResponseEntity<SystemItemHistoryResponse> getUpdates(@RequestParam String date) {
        modelValidator.validateDate(date);
        return new ResponseEntity<>(itemService.getUpdates(date), HttpStatus.OK);
    }

    /**
     * Получение истории версий для объекта system_item.
     *
     * @param id        идентификатор system_item.
     * @param dateStart дата начала фильтрации.
     * @param dateEnd   дата окончания фильтрации.
     * @return {@link SystemItemHistoryResponse}.
     */
    @GetMapping(value = Endpoints.ITEM_HISTORY)
    public ResponseEntity<SystemItemHistoryResponse> getHistoryForItem(@RequestParam String dateStart,
                                                                       @RequestParam String dateEnd,
                                                                       @PathVariable String id) {
        modelValidator.validateDate(dateStart);
        modelValidator.validateDate(dateEnd);

        return new ResponseEntity<>(itemService.getHistoryForItem(id, dateStart, dateEnd), HttpStatus.OK);
    }
}
