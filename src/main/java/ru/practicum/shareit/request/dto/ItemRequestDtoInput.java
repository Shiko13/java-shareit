package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ItemRequestDtoInput {

    private Long id;

    @NotBlank
    @NotNull
    private String description;

    private Long requestorId;

    private LocalDateTime created;
}
