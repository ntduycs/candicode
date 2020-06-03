package vn.candicode.payloads.validators;

import vn.candicode.models.enums.GenericEnum;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PACKAGE, ElementType.FIELD, ElementType.PARAMETER})
@Constraint(validatedBy = Belong2EnumValidator.class)
public @interface Belong2Enum {
    String message() default "Given value does not belong to corresponding enum";

    Class<? extends GenericEnum> target();

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
