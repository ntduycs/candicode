package vn.candicode.models.converters;

import com.google.common.base.Joiner;
import vn.candicode.models.enums.AdminRole;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Converter
public class List2StringConverter implements AttributeConverter<List<AdminRole>, String> {
    @Override
    public String convertToDatabaseColumn(List<AdminRole> enums) {
        List<String> list = new ArrayList<>();
        for (AdminRole element : enums) {
            list.add(element.toString());
        }
        return Joiner.on(",").join(list);
    }

    @Override
    public List<AdminRole> convertToEntityAttribute(String s) {
        return Arrays.stream(s.split(",")).map(AdminRole::valueOf).collect(Collectors.toList());
    }
}
