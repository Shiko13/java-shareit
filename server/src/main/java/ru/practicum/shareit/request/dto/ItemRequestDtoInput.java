package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ItemRequestDtoInput {

    private Long id;

    private String description;

    private Long requestorId;

    private LocalDateTime created;
}
