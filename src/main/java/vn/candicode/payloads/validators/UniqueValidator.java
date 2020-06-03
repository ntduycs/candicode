package vn.candicode.payloads.validators;

import org.springframework.util.StringUtils;
import vn.candicode.payloads.services.UniqueDBRecordValidator;
import vn.candicode.utils.BeanUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class UniqueValidator implements ConstraintValidator<Unique, Object> {
    private String column;
    private UniqueDBRecordValidator validationService;

    @Override
    public void initialize(Unique constraintAnnotation) {
        this.column = constraintAnnotation.column();

        Class<? extends UniqueDBRecordValidator> validationService = constraintAnnotation.service();

        if (StringUtils.isEmpty(constraintAnnotation.qualifier())) {
            this.validationService = BeanUtils.getBean(validationService);
        } else {
            this.validationService = BeanUtils.getBean(constraintAnnotation.qualifier(), validationService);
        }
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext constraintValidatorContext) {
        return validationService.isUnique(column, value);
    }
}
