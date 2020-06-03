package vn.candicode.controllers;

import org.springframework.boot.autoconfigure.web.servlet.error.AbstractErrorController;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;

@RestController
public class ErrorController extends AbstractErrorController {
    private static final boolean INCLUDE_STACKTRACE = false;

    public ErrorController(ErrorAttributes errorAttributes) {
        super(errorAttributes, Collections.emptyList());
    }

    @Override
    public String getErrorPath() {
        return "/error";
    }

    @RequestMapping(path = "/error")
    public ResponseEntity<?> returnErrorAsJson(HttpServletRequest request) {
        return new ResponseEntity<>(getErrorAttributes(request, INCLUDE_STACKTRACE), getStatus(request));
    }
}
