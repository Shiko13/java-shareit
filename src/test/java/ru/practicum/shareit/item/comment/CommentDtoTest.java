package ru.practicum.shareit.item.comment;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class CommentDtoTest {

    @Autowired
    private JacksonTester<CommentDto> jacksonTester;

    @Test
    void serialization() throws IOException {
        CommentDto comment = new CommentDto(1L, "First, nah!",
                "Speedy Gonzalez", LocalDateTime.now());
        JsonContent<CommentDto> result = jacksonTester.write(comment);

        assertThat(result).hasJsonPath("$.id");
        assertThat(result).hasJsonPath("$.text");
        assertThat(result).hasJsonPath("$.authorName");
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(comment.getId().intValue());
        assertThat(result).extractingJsonPathValue("$.text").isEqualTo(comment.getText());
        assertThat(result).extractingJsonPathValue("$.authorName").isEqualTo(comment.getAuthorName());
    }
}