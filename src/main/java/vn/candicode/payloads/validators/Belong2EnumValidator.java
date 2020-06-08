package vn.candicode.payloads.validators;

import lombok.extern.log4j.Log4j2;
import vn.candicode.models.enums.GenericEnum;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

@Log4j2
public class Belong2EnumValidator extends GenericValidator implements ConstraintValidator<Belong2Enum, Object> {
    private Class<? extends GenericEnum> clazz;

    @Override
    public void initialize(Belong2Enum constraintAnnotation) {
        this.clazz = constraintAnnotation.target();
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean isValid(Object o, ConstraintValidatorContext constraintValidatorContext) {
        if (o instanceof String) {
            return isValid(o.toString());
        } else if (o instanceof List) {
            return isValid((List<String>) o);
        } else {
            return NO_NEED_VALIDATE;
        }
    }

    private boolean isValid(String str) {
        if (str == null) {
            return NO_NEED_VALIDATE;
        }

        try {
            clazz.getDeclaredMethod("valueOf", String.class).invoke(null, str);
            return VALID;
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            log.error(e.getMessage());
            return NO_NEED_VALIDATE;
        } catch (IllegalArgumentException e) {
            log.info(e.getMessage());
            return INVALID;
        }
    }

    private boolean isValid(List<String> lst) {
        if (lst == null || lst.isEmpty()) {
            return NO_NEED_VALIDATE;
        }

        return lst.stream().anyMatch(str -> !isValid(str));
    }
}
