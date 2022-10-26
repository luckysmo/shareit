package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoForCreate;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {

    private final ItemDto itemDto = ItemDto.builder()
            .id(1L)
            .name("item")
            .description("description")
            .available(true)
            .build();
    private final ItemDtoForCreate itemDtoForCreate = ItemDtoForCreate.builder()
            .id(1L)
            .name("item")
            .description("description")
            .available(true)
            .build();
    @Autowired
    private ObjectMapper mapper;
    @MockBean
    private ItemService service;
    @Autowired
    private MockMvc mvc;

    @Test
    void whenGetItemExist_thenReturnItemStatus2xx() throws Exception {
        when(service.getById(1L, itemDto.getId())).thenReturn(itemDto);

        mvc.perform(get("/items/" + itemDto.getId())
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDtoForCreate.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDtoForCreate.getName())))
                .andExpect(jsonPath("$.description", is(itemDtoForCreate.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDtoForCreate.getAvailable())));
    }

    @Test
    void whenGetOwnItems_thenReturnLiatOfOwnItemsStatus2xx() throws Exception {
        List<ItemDto> itemsDto = new ArrayList<>();

        for (int i = 1; i < 4; i++) {
            ItemDto item = ItemDto.builder()
                    .id((long) i)
                    .name("item" + i)
                    .description("description")
                    .available(true)
                    .build();
            itemsDto.add(item);
        }

        when(service.getAllItemsOfOneUser(anyLong(), anyInt(), anyInt())).thenReturn(itemsDto);

        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .param("from", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(itemsDto.size()));
    }

    @Test
    void whenCreateItem_thenReturnItemStatus200() throws Exception {
        when(service.addNewItem(1L, itemDtoForCreate)).thenReturn(itemDtoForCreate);

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())));
    }

    @Test
    void whenUpdateItem_thenReturnUpdatedItemStatus200() throws Exception {
        ItemDtoForCreate newItemName = ItemDtoForCreate.builder()
                .name("newName")
                .build();

        ItemDtoForCreate updatedItem = ItemDtoForCreate.builder()
                .id(itemDto.getId())
                .name(newItemName.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .build();

        when(service.update(1L, itemDto.getId(), newItemName)).thenReturn(updatedItem);

        mvc.perform(patch("/items/" + itemDto.getId())
                        .content(mapper.writeValueAsString(newItemName))
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(updatedItem.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(updatedItem.getName())))
                .andExpect(jsonPath("$.description", is(updatedItem.getDescription())))
                .andExpect(jsonPath("$.available", is(updatedItem.getAvailable())));
    }

    @Test
    void whenSearchItem_thenReturnListOfItemsStatus2xx() throws Exception {
        List<ItemDtoForCreate> itemsDto = createItemsInputDto();

        when(service.searchItem(anyString(), anyInt(), anyInt())).thenReturn(itemsDto);

        mvc.perform(get("/items/search")
                        .param("text", "item")
                        .param("from", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(itemsDto.size()));
    }

        @Test
    void whenSearchItemWithoutFromAndSize_thenReturnListOfItemsWithDefaultFromAndSize() throws Exception {
        List<ItemDtoForCreate> itemsDto = createItemsInputDto();

        when(service.searchItem(anyString(), anyInt(), anyInt())).thenReturn(itemsDto);

        mvc.perform(get("/items/search")
                        .param("text", "item")
                        .param("from", "")
                        .param("size", ""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(itemsDto.size()));
    }

    @Test
    void createComment() throws Exception {
        CommentDto commentDto = CommentDto.builder()
                .id(1L)
                .text("text")
                .authorName("author")
                .build();

        when(service.createComment(anyLong(), anyLong(), any())).thenReturn(commentDto);

        mvc.perform(post("/items/" + itemDto.getId() + "/comment")
                        .content(mapper.writeValueAsString(commentDto))
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentDto.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(commentDto.getText())))
                .andExpect(jsonPath("$.authorName", is(commentDto.getAuthorName())));
    }

    private List<ItemDtoForCreate> createItemsInputDto() {
        List<ItemDtoForCreate> itemsDto = new ArrayList<>();
        for (int i = 1; i < 4; i++) {
            ItemDtoForCreate item = ItemDtoForCreate.builder()
                    .id((long) i)
                    .name("item" + i)
                    .description("description")
                    .available(true)
                    .build();
            itemsDto.add(item);
        }
        return itemsDto;
    }
}
