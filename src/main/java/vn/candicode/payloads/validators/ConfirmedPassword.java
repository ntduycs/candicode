package vn.candicode.payloads.validators;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Constraint(validatedBy = ConfirmedPasswordValidator.class)
public @interface ConfirmedPassword {
    String message() default "Confirm password does not match password";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
