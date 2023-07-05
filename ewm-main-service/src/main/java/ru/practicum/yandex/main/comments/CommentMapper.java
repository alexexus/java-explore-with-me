package ru.practicum.yandex.main.comments;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.yandex.main.comments.dto.CommentDto;
import ru.practicum.yandex.main.comments.dto.NewCommentDto;
import ru.practicum.yandex.main.events.EventMapper;
import ru.practicum.yandex.main.users.UserMapper;

@Component
@AllArgsConstructor
public class CommentMapper {

    private final UserMapper userMapper;
    private final EventMapper eventMapper;

    public CommentDto toCommentDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .author(userMapper.toUserShortDto(comment.getAuthor()))
                .event(eventMapper.toEventShortDto(comment.getEvent()))
                .text(comment.getText())
                .created(comment.getCreated())
                .edited(comment.getEdited())
                .build();
    }

    public Comment toComment(NewCommentDto newCommentDto) {
        return Comment.builder()
                .text(newCommentDto.getText())
                .build();
    }
}
