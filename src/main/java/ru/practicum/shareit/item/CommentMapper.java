package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentMapper {
    public static CommentDto mapToCommentDto(Comment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getText(),
                comment.getAuthor().getName(),
                LocalDateTime.now());
    }

    public static Comment mapToComment(CommentDto commentDto, User author, Item item) {
        return new Comment(
                commentDto.getId(),
                commentDto.getText(),
                item,
                author);
    }

    public static List<CommentDto> mapToListCommentsDto(List<Comment> comments) {
        List<CommentDto> result = new ArrayList<>();
        for (Comment comment : comments) {
            result.add(mapToCommentDto(comment));
        }
        return result;
    }
}
