package vn.candicode.payloads.validators;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER, ElementType.FIELD})
@Constraint(validatedBy = FileValidator.class)
public @interface File {
    String message() default "";

    boolean required() default true;

    long maxSize() default 10485760L; // 10MB

    String[] mimes() default {};

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payloads() default {};

    @Documented
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.FIELD, ElementType.PARAMETER})
    @interface List {
        File[] value();
    }
}
