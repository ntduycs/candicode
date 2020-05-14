package vn.candicode.controllers;

import org.springframework.boot.autoconfigure.web.servlet.error.AbstractErrorController;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;

@RestController
@RequestMapping(path = "/error")
public class ErrorController extends AbstractErrorController {
    public ErrorController(ErrorAttributes errorAttributes) {
        super(errorAttributes, Collections.emptyList());
    }

    @Override
    public String getErrorPath() {
        return "/error";
    }

    @RequestMapping(path = "")
    public ResponseEntity<?> handle(HttpServletRequest request) {
        return new ResponseEntity<>(getErrorAttributes(request, false), getStatus(request));
    }
}
