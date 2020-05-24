package vn.candicode.models.converters;

import com.google.common.base.Joiner;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Converter
public class Testcases2StringConverter implements AttributeConverter<List<String>, String> {
    @Override
    public String convertToDatabaseColumn(List<String> strings) {
        return Joiner.on("\t").join(strings);
    }

    @Override
    public List<String> convertToEntityAttribute(String s) {
        return Arrays.stream(s.split("\\t")).collect(Collectors.toList());
    }
}
