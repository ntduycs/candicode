package vn.candicode.payloads.validators;

import lombok.extern.log4j.Log4j2;
import org.springframework.util.StringUtils;
import vn.candicode.models.enums.BaseEnum;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.reflect.InvocationTargetException;

@Log4j2
public class EnumValidator implements ConstraintValidator<Enum, String> {
    private Class<? extends BaseEnum> clazz;

    @Override
    public void initialize(Enum constraintAnnotation) {
        this.clazz = constraintAnnotation.target();
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext context) {
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

        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate("Given value not found in enum " + clazz.getSimpleName());

        return false;
    }
}
