package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemAllDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {
    @MockBean
    ItemService itemService;

    @Autowired
    ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    private ItemAllDto itemAllDto;
    private ItemDto itemDto;
    private CommentDto commentDto;
    private final String header = "X-Sharer-User-Id";

    @BeforeEach
    void setUp() {
        itemDto = new ItemDto(1L, "name", "description",
                true, null, 1L);
        itemAllDto = new ItemAllDto(1L, "name", "description",
                true, null, 1L, null, null, null);
        commentDto = new CommentDto(1L, "comment", 1L, "author", LocalDateTime.now());
    }

    @Test
    void createItem() throws Exception {
        when(itemService.createItem(anyLong(), any())).thenReturn(itemDto);
        mvc.perform(post("/items").header(header, 1)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())))
                .andExpect(jsonPath("$.ownerId", is(itemDto.getOwnerId()), Long.class));
    }

    @Test
    void updateItem() throws Exception {
        when(itemService.updateItem(anyLong(), anyLong(), any())).thenReturn(itemDto);
        mvc.perform(patch("/items/{id}", 1).header(header, 1)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())))
                .andExpect(jsonPath("$.ownerId", is(itemDto.getOwnerId()), Long.class));

    }

    @Test
    void getItem() throws Exception {
        when(itemService.getItem(anyLong(), anyLong())).thenReturn(itemAllDto);
        mvc.perform(get("/items/{id}", 1).header(header, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemAllDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemAllDto.getName())))
                .andExpect(jsonPath("$.description", is(itemAllDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemAllDto.getAvailable())))
                .andExpect(jsonPath("$.ownerId", is(itemAllDto.getOwnerId()), Long.class));
    }

    @Test
    void getItems() throws Exception {
        when(itemService.getItems(anyLong())).thenReturn(List.of(itemAllDto));
        mvc.perform(get("/items", 1).header(header, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(1)))
                .andExpect(jsonPath("$[0].id", is(itemAllDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemAllDto.getName())));
    }

    @Test
    void searchItems() throws Exception {
        when(itemService.findItems(anyString())).thenReturn(List.of(itemDto));
        mvc.perform(get("/items/search").header(header, 1).param("text", anyString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(1)))
                .andExpect(jsonPath("$[0].id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemDto.getName())));
    }

    @Test
    void addComment() throws Exception {
        when(itemService.addComment(anyLong(), anyLong(), any())).thenReturn(commentDto);
        mvc.perform(post("/items/{id}/comment", 1).header(header, 1)
                        .content(mapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentDto.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(commentDto.getText())))
                .andExpect(jsonPath("$.itemId", is(commentDto.getItemId()), Long.class))
                .andExpect(jsonPath("$.authorName", is(commentDto.getAuthorName())));
    }

    @Test
    void getItemNotFoundException() throws Exception {
        when(itemService.getItem(anyLong(), anyLong()))
                .thenThrow(new NotFoundException("Item not found"));
        mvc.perform(get("/items/{id}", 99).header(header, 1))
                .andExpect(status().isNotFound());
    }
}
