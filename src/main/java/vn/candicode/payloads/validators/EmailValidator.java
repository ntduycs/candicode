package vn.candicode.payloads.validators;

import org.springframework.util.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class EmailValidator extends GenericValidator implements ConstraintValidator<Email, String> {
    private static final Pattern EMAIL_FORMAT = Pattern.compile("^[-a-z0-9~!$%^&*_=+}{'?]+(\\.[-a-z0-9~!$%^&*_=+}{'?]+)*@([a-z0-9_][-a-z0-9_]*(\\.[-a-z0-9_]+)*\\.(aero|arpa|biz|com|coop|edu|gov|info|int|mil|museum|name|net|org|pro|travel|mobi|[a-z][a-z])|([0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}))(:[0-9]{1,5})?$");

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        if (!StringUtils.hasText(s)) {
            return NO_NEED_VALIDATE;
        }

        return EMAIL_FORMAT.matcher(s).matches();
    }
}
