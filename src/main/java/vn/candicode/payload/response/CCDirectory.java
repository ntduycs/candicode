package vn.candicode.payload.response;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
@JsonPropertyOrder(value = {"label", "value", "type", "children"})
public class CCDirectory implements CCFile {
    private final String label;
    private final String value;
    private final List<CCFile> children;
    private final String type = "directory";
}
