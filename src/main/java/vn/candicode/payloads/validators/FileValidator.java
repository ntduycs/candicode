package vn.candicode.payloads.validators;

import org.springframework.web.multipart.MultipartFile;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.List;

public class FileValidator implements ConstraintValidator<File, MultipartFile> {
    private static final String requiredMessage = "No file chosen or file is empty";
    private static final String maxSizeMessage = "File exceeds the maximum allowed file size";
    private static final String mimesMessage = "Unsupported file type";

    private boolean required;
    private long maxSize;
    private List<String> mimes;

    @Override
    public void initialize(File constraintAnnotation) {
        this.required = constraintAnnotation.required();
        this.maxSize = constraintAnnotation.maxSize();
        this.mimes = Arrays.asList(constraintAnnotation.mimes());
    }

    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext constraintValidatorContext) {

        if (required && (file == null || file.isEmpty())) {
            constraintValidatorContext.buildConstraintViolationWithTemplate(requiredMessage);
            return false;
        }

        if (file != null) {
            if (maxSize > -1 && file.getSize() > maxSize) {
                constraintValidatorContext.buildConstraintViolationWithTemplate(maxSizeMessage);
                return false;
            } else if (!mimes.isEmpty() && !mimes.contains(file.getContentType())) {
                constraintValidatorContext.buildConstraintViolationWithTemplate(mimesMessage);
                return false;
            }
        }

        return true;
    }
}
