package ru.practicum.shareit.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import ru.practicum.shareit.util.StringToStateConverter;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final StringToStateConverter stringToStateConverter;

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(stringToStateConverter);
    }
}
