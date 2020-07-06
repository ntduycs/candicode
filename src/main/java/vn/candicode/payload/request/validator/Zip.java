package vn.candicode.payload.request.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.TYPE_PARAMETER, ElementType.LOCAL_VARIABLE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {ZipValidator.class})
public @interface Zip {
    String message() default "Only ZIP files are allowed.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
