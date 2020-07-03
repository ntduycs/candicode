package vn.candicode.converter;

import com.google.common.base.Joiner;
import com.google.common.collect.Sets;
import org.springframework.util.StringUtils;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.HashSet;
import java.util.Set;

@Converter
public class TagConverter implements AttributeConverter<Set<String>, String> {
    @Override
    public String convertToDatabaseColumn(Set<String> strings) {
        return strings != null ? Joiner.on(",").join(strings) : null;
    }

    @Override
    public Set<String> convertToEntityAttribute(String s) {
        return StringUtils.hasText(s) ? Sets.newHashSet(s.split(",")) : new HashSet<>();
    }
}
