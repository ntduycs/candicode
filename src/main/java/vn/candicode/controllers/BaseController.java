package vn.candicode.controllers;

import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Map;

public abstract class BaseController {
    protected abstract String getPath();

    protected URI location(Long resourceId) {
        return ServletUriComponentsBuilder
            .fromCurrentContextPath()
            .path("/{path}/{id}")
            .buildAndExpand(Map.of("path", getPath(), "id", resourceId))
            .toUri();
    }
}
