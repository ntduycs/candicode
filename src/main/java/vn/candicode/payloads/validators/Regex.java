package vn.candicode.payloads.validators;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER, ElementType.FIELD})
@Constraint(validatedBy = RegexValidator.class)
public @interface Regex {
    String message() default "Invalid regular expression format";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payloads() default {};
}
