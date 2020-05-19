package vn.candicode.payloads.validators;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER, ElementType.FIELD})
@Constraint(validatedBy = UniqueValidator.class)
public @interface Unique {
    String message();

    Class<? extends UniqueValidatorService> service();

    String column();

    String qualifier() default "";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
