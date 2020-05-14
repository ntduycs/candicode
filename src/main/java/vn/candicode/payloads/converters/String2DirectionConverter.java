package vn.candicode.payloads.converters;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.lang.NonNull;

public class String2DirectionConverter implements Converter<String, Direction> {
    @Override
    public Direction convert(@NonNull String s) {
        return Direction.fromString(s);
    }
}
