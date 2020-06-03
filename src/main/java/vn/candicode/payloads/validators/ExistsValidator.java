package vn.candicode.payloads.validators;

import org.springframework.util.StringUtils;
import vn.candicode.payloads.services.ExistDBRecordValidator;
import vn.candicode.utils.BeanUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ExistsValidator implements ConstraintValidator<Exists, Object> {
    private String column;
    private ExistDBRecordValidator validatorService;

    @Override
    public void initialize(Exists constraintAnnotation) {
        this.column = constraintAnnotation.column();

        Class<? extends ExistDBRecordValidator> validationService = constraintAnnotation.service();

        if (StringUtils.isEmpty(constraintAnnotation.qualifier())) {
            this.validatorService = BeanUtils.getBean(validationService);
        } else {
            this.validatorService = BeanUtils.getBean(constraintAnnotation.qualifier(), validationService);
        }
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext constraintValidatorContext) {
        return validatorService.isExist(column, value);
    }
}
