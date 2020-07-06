package vn.candicode.payload.request.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = ConfirmPasswordValidator.class)
public @interface PasswordConfirm {
    String message() default "Confirm password does not match";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
