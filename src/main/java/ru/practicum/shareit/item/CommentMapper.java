package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CommentMapper {
    public static CommentDto mapToCommentDto(Comment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getText(),
                comment.getAuthor().getName(),
                LocalDateTime.now());
    }

    public static List<CommentDto> mapToListCommentsDto(List<Comment> comments) {
        List<CommentDto> result = new ArrayList<>();
        for (Comment comment : comments) {
            result.add(mapToCommentDto(comment));
        }
        return result;
    }
}
