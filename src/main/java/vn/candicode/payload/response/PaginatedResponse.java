package vn.candicode.payload.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
public class PaginatedResponse<T> implements Serializable {
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean first;
    private boolean last;
    private List<T> items;

    public static PaginatedResponse<?> empty() {
        return PaginatedResponse.builder()
            .page(0)
            .size(10)
            .totalPages(0)
            .totalElements(0)
            .first(true)
            .last(true)
            .items(new ArrayList<>())
            .build();
    }
}
