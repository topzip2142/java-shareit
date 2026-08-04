package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentResponseDTO {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private long id;
    private String text;
    private String authorName;
    private LocalDateTime created;
}