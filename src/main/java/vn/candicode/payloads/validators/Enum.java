package vn.candicode.payloads.validators;

import vn.candicode.models.enums.BaseEnum;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PACKAGE, ElementType.FIELD})
@Constraint(validatedBy = EnumValidator.class)
public @interface Enum {
    String message() default "Given value does not belong to corresponding enum";

    Class<? extends BaseEnum> target();

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
