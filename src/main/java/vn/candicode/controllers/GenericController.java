package vn.candicode.controllers;

import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

    protected Pageable getPaginationConfig(int page, int size, String sortBy, String drt) {
        Sort.Direction direction;
        try {
            direction = Sort.Direction.fromString(drt);
        } catch (IllegalArgumentException e) {
            direction = Sort.Direction.DESC;
        }

        Sort sortConfig = sortBy != null
            ? Sort.by(direction, sortBy)
            : Sort.unsorted();

        return PageRequest.of(page, size, sortConfig);
    }
}
