package vn.candicode.payloads.validators;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class EmailValidator implements ConstraintValidator<Email, String> {
    private static final Pattern EMAIL_FORMAT =
        Pattern.compile("^[A-Za-z0-9]+(.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(.[A-Za-z0-9]+)*(.[A-Za-z]{2,})$");


    /**
     * @param email need-validate string
     * @param context validator context
     * @return true if match valid email format. in case of empty string, return true
     */
    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        if (email == null) {
            return true;
        }

        return EMAIL_FORMAT.matcher(email).matches();
    }
}
