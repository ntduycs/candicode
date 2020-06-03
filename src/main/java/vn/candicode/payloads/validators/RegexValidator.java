package vn.candicode.payloads.validators;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class RegexValidator extends GenericValidator implements ConstraintValidator<Regex, String> {
    @Override
    public boolean isValid(String regex, ConstraintValidatorContext constraintValidatorContext) {
        if (regex == null) {
            return NO_NEED_VALIDATE;
        }

        try {
            Pattern.compile(regex);
            return VALID;
        } catch (PatternSyntaxException e) {
            return INVALID;
        }
    }
}
