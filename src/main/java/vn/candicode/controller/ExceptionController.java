package vn.candicode.controller;

import org.springframework.boot.autoconfigure.web.servlet.error.AbstractErrorController;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;

public class ExceptionController extends AbstractErrorController {
    public ExceptionController(ErrorAttributes errorAttributes) {
        super(errorAttributes, Collections.emptyList());
    }

    @Override
    public String getErrorPath() {
        return "error";
    }

    @RequestMapping(path = "error")
    public ResponseEntity<?> handle(HttpServletRequest httpServletRequest) {
        return new ResponseEntity<>(getErrorAttributes(httpServletRequest, false), getStatus(httpServletRequest));
    }
}
