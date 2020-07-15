package vn.candicode.controller;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import vn.candicode.payload.request.PaginatedRequest;

import java.net.URI;
import java.util.Map;

public abstract class Controller {
    protected abstract String getPath();

    protected URI getResourcePath(Long resourceId) {
        return ServletUriComponentsBuilder
            .fromCurrentContextPath()
            .path("/{path}/{resourceId}")
            .buildAndExpand(Map.of("path", getPath(), "resourceId", resourceId))
            .toUri();
    }

    protected Pageable getPaginationConfig(int page, int size, String sortBy, String drt) {
        if (page < 1) page = 1;
        if (size < 1) size = 10;

        Sort.Direction direction = StringUtils.hasText(drt) && drt.equals("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;

        Sort sortConfig = StringUtils.hasText(sortBy)
            ? Sort.by(direction, sortBy)
            : Sort.unsorted();

        return PageRequest.of(page - 1, size, sortConfig);
    }

    public static Pageable getPaginationConfig(PaginatedRequest payload) {
        if (payload.getPage() < 1) payload.setPage(1);
        if (payload.getSize() < 1) payload.setSize(10);

        Sort.Direction direction = StringUtils.hasText(payload.getDirection()) && payload.getDirection().equals("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;

        Sort sortConfig = StringUtils.hasText(payload.getSort())
            ? Sort.by(direction, payload.getSort())
            : Sort.unsorted();

        return PageRequest.of(payload.getPage() - 1, payload.getSize(), sortConfig);
    }
}
