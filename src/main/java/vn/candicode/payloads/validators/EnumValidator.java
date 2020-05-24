package vn.candicode.payloads.validators;

import lombok.extern.log4j.Log4j2;
import org.springframework.util.StringUtils;
import vn.candicode.models.enums.BaseEnum;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
public class EnumValidator implements ConstraintValidator<Enum, Object> {
    private Class<? extends BaseEnum> clazz;

    @Override
    public void initialize(Enum constraintAnnotation) {
        this.clazz = constraintAnnotation.target();
    }

    @Override
    public boolean isValid(Object s, ConstraintValidatorContext context) {
        boolean valid = true;
        if (s instanceof String) {
            valid = isStringValid(s.toString(), context);
        } else if (s instanceof List) {
            valid = isListValid((List<String>) s, context);
        }

        return valid;
    }

    private boolean isStringValid(String s, ConstraintValidatorContext context) {
        if (!StringUtils.hasText(s)) {
            return true;
        }

        try {
            clazz.getDeclaredMethod("valueOf", String.class).invoke(null, s.toUpperCase());
            return true;
        } catch (IllegalArgumentException ignored) {
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            log.error(e.getMessage());
        }

        return false;
    }

    private boolean isListValid(List<String> list, ConstraintValidatorContext context) {
        if (list == null || list.isEmpty()) {
            return true;
        }

        boolean valid = true;

        for (String ele: list) {
            if (!isStringValid(ele, context)) {
                valid = false;
                break;
            }
        }

        return valid;
    }
}
