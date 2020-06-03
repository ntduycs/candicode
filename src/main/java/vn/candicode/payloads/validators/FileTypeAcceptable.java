package vn.candicode.payloads.validators;

import vn.candicode.common.filesystem.FileType;

import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.TYPE})
public @interface FileTypeAcceptable {
    String message() default "File type is not acceptable";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    FileType[] value();
}
