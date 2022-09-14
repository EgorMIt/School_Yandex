package com.example.school_yandex;

import com.example.school_yandex.application.dto.SystemItemImport;
import com.example.school_yandex.application.dto.SystemItemImportRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


/**
 * Тестирование web-уровня.
 *
 * @author Egor Mitrofanov.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SchoolYandexApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Проверка импорта объектов")
    public void importsTest() throws Exception {
        List<SystemItemImport> items = new ArrayList<>();
        items.add(SystemItemImport.of("testFolderForImports", "/file1", null, "FOLDER", null));
        items.add(SystemItemImport.of("testFileForImports", "/file2", "testFolderForImports", "FILE", 55L));
        String date = "1980-02-01T12:00:00Z";

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/imports")
                        .content(asJsonString(SystemItemImportRequest.of(items, date)))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/nodes/{id}", "testFolderForImports")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.size").value(55));

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/delete/{id}", "testFolderForImports")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/nodes/{id}", "testFolderForImports")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Проверка на Bad Request (Неверный формат даты)")
    public void checkWrongDate() throws Exception {
        List<SystemItemImport> items = new ArrayList<>();
        items.add(SystemItemImport.of("1_1", "/file1", null, "FOLDER", null));
        String date = "3 Jun 2008 11:05:30";

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/imports")
                        .content(asJsonString(SystemItemImportRequest.of(items, date)))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Проверка на Bad Request (Невалидный размер файла)")
    public void checkWrongSize() throws Exception {
        List<SystemItemImport> items = new ArrayList<>();
        items.add(SystemItemImport.of("1_1", "/file1", null, "FILE", -1L));
        String date = "2022-02-01T12:00:00Z";

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/imports")
                        .content(asJsonString(SystemItemImportRequest.of(items, date)))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Проверка на Bad Request (Невалидный URL)")
    public void checkWrongUrl() throws Exception {
        List<SystemItemImport> items = new ArrayList<>();
        items.add(SystemItemImport.of("1_1", "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean commodo ligula eget dolor. Aenean massa. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Donec quam felis, ultricies nec, pellentesque eu, pretium quis, sem. Nulla consequat massa quis enim. Donec.",
                null, "FILE", 1L));
        String date = "2022-02-01T12:00:00Z";

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/imports")
                        .content(asJsonString(SystemItemImportRequest.of(items, date)))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Проверка запроса /updates")
    public void checkUpdatesRequest() throws Exception {
        List<SystemItemImport> items = new ArrayList<>();
        items.add(SystemItemImport.of("testFileForUpdates", "/file", null, "FILE", 1L));
        String date = "1970-02-01T12:00:00Z";

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/imports")
                        .content(asJsonString(SystemItemImportRequest.of(items, date)))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        items.clear();
        items.add(SystemItemImport.of("testFileForUpdates", "/file", null, "FILE", 5L));
        date = "1970-02-01T13:00:00Z";

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/imports")
                        .content(asJsonString(SystemItemImportRequest.of(items, date)))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        items.clear();
        items.add(SystemItemImport.of("testFileForUpdates", "/file/newUrl", null, "FILE", 5L));
        date = "1970-02-01T14:00:00Z";

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/imports")
                        .content(asJsonString(SystemItemImportRequest.of(items, date)))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        String updateDate = "1970-02-01T20:00:00Z";

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/updates")
                        .param("date", updateDate)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].id").value("testFileForUpdates"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].size").value(5))
                .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].date").value("1970-02-01T14:00:00Z"));

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/delete/{id}", "testFileForUpdates")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/updates")
                        .param("date", updateDate)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.items").isEmpty());
    }

    @Test
    @DisplayName("Проверка запроса /node/history")
    public void checkNodeHistoryRequest() throws Exception {
        List<SystemItemImport> items = new ArrayList<>();
        items.add(SystemItemImport.of("testFileForHistory", "/file", null, "FILE", 1L));
        String date = "1960-02-01T12:00:00Z";

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/imports")
                        .content(asJsonString(SystemItemImportRequest.of(items, date)))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        items.clear();
        items.add(SystemItemImport.of("testFileForHistory", "/file", null, "FILE", 5L));
        date = "1960-02-01T13:00:00Z";

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/imports")
                        .content(asJsonString(SystemItemImportRequest.of(items, date)))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        items.clear();
        items.add(SystemItemImport.of("testFileForHistory", "/file/newUrl", null, "FILE", 5L));
        date = "1960-02-01T14:00:00Z";

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/imports")
                        .content(asJsonString(SystemItemImportRequest.of(items, date)))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        String dateStart = "1960-02-01T01:00:00Z";
        String dateEnd = "1960-02-03T20:00:00Z";


        mockMvc.perform(MockMvcRequestBuilders
                        .get("/node/{id}/history", "testFileForHistory")
                        .param("dateStart", dateStart)
                        .param("dateEnd", dateEnd)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].id").value("testFileForHistory"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].size").value(5))
                .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].url").value("/file/newUrl"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].date").value("1960-02-01T14:00:00Z"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.items[1].size").value(5))
                .andExpect(MockMvcResultMatchers.jsonPath("$.items[1].url").value("/file"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.items[1].date").value("1960-02-01T13:00:00Z"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.items[2].size").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.items[2].date").value("1960-02-01T12:00:00Z"));


        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/delete/{id}", "testFileForHistory")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/node/{id}/history", "testFileForHistory")
                        .param("dateStart", dateStart)
                        .param("dateEnd", dateEnd)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
