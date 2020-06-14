package vn.candicode.models.converters;

import com.google.common.base.Joiner;
import org.springframework.util.StringUtils;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Converter
public class TagConverter implements AttributeConverter<List<String>, String> {
    @Override
    public String convertToDatabaseColumn(List<String> strings) {
        return Joiner.on(",").join(strings);
    }

    @Override
    public List<String> convertToEntityAttribute(String s) {
        if (StringUtils.isEmpty(s)) {
            return new ArrayList<>();
        }

        List<String> ret = new ArrayList<>();
        String[] tags = s.split(",");
        Collections.addAll(ret, tags);
        return ret;
    }
}
