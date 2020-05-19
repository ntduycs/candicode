package vn.candicode.payloads.validators;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER, ElementType.FIELD})
@Constraint(validatedBy = ExistsValidator.class)
public @interface Exists {
    String message();

    Class<? extends ExistenceValidatorService> service();

    String column();

    String qualifier() default "";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
