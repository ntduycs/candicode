package vn.candicode.payload.request.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {ImageValidator.class})
public @interface Image {
    String message() default "Only PNG, JPG, or JPEG images are allowed.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
