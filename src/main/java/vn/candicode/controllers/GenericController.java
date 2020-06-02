package vn.candicode.controllers;

import lombok.extern.log4j.Log4j2;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Map;

@Log4j2
public abstract class GenericController {
    protected abstract String getResourceBasePath();

    protected URI getResourcePath(Long resourceId) {
        return ServletUriComponentsBuilder
            .fromCurrentContextPath()
            .path("/{basePath}/{resourceId}")
            .buildAndExpand(Map.of("basePath", getResourceBasePath(), "resourceId", resourceId))
            .toUri();
    }
}
