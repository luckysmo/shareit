package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.requests.ItemRequestController;
import ru.practicum.shareit.requests.ItemRequestService;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.dto.ItemRequestDtoWithItems;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ItemRequestService service;

    @Autowired
    private MockMvc mvc;

    @Test
    void whenGetByIdExist_thenReturnItemRequest() throws Exception {
        ItemRequestDtoWithItems requestDto = createRequestDtoWithTime(1L);

        when(service.getRequestById(1L, 1L))
                .thenReturn(requestDto);

        mvc.perform(get("/requests/" + requestDto.getId())
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(requestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(requestDto.getDescription())));
    }

    @Test
    void whenGetRequestsOfCurrentUser_thenReturnListOfRequests() throws Exception {
        List<ItemRequestDtoWithItems> ownRequests = createRequestsDtoWithItems();

        when(service.getRequestsOfCurrentUser(1L)).thenReturn(ownRequests);

        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(ownRequests.size()));
    }

    @Test
    void whenGetAllRequests_thenReturnListOfRequests() throws Exception {
        List<ItemRequestDtoWithItems> requests = createRequestsDtoWithItems();

        when(service.getAllRequests(0, 20, 1L)).thenReturn(requests);

        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1L)
                        .param("from", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(requests.size()));
    }

    @Test
    void whenCreateRequest_thenReturnItemRequest() throws Exception {
        ItemRequestDto requestDto = createRequestDto();

        when(service.addRequest(1L, requestDto))
                .thenReturn(requestDto);

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(requestDto))
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(requestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(requestDto.getDescription())));
    }

    private ItemRequestDtoWithItems createRequestDtoWithTime(Long id) {
        return ItemRequestDtoWithItems.builder()
                .id(id)
                .description("description")
                .created(LocalDateTime.now())
                .build();
    }

    private ItemRequestDto createRequestDto() {
        return ItemRequestDto.builder()
                .id(1L)
                .description("description")
                .created(LocalDateTime.now())
                .build();
    }

    private List<ItemRequestDtoWithItems> createRequestsDtoWithItems() {
        return List.of(
                createRequestDtoWithTime(1L),
                createRequestDtoWithTime(2L),
                createRequestDtoWithTime(3L)
        );
    }
}
