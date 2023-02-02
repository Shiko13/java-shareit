package ru.practicum.shareit.util;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.State;

import org.springframework.core.convert.converter.Converter;

@Component
public class StringToStateConverter implements Converter<String, State> {

    @Override
    public State convert(String source) {
        try {
            return State.valueOf(source.toUpperCase());
        } catch (Exception e) {
            return State.UNSUPPORTED_STATUS;
        }
    }
}
