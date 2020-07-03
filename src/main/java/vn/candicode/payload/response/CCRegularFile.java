package vn.candicode.payload.response;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
@JsonPropertyOrder(value = {"label", "value", "type"})
public class CCRegularFile implements CCFile {
    private final String label;
    private final String value;
    private final String type = "file";
}
