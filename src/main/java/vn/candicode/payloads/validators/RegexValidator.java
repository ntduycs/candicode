package vn.candicode.payloads.validators;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class RegexValidator implements ConstraintValidator<Regex, String> {
    @Override
    public boolean isValid(String regex, ConstraintValidatorContext constraintValidatorContext) {
        if (regex == null) {
            return true; // Left for NotNull validator
        }

        try {
            Pattern.compile(regex);
            return true;
        } catch (PatternSyntaxException e) {
            return false;
        }
    }
}
