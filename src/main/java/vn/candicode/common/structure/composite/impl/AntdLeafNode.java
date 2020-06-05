package vn.candicode.common.structure.composite.impl;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import vn.candicode.common.structure.composite.LeafNode;

@Getter
@JsonPropertyOrder(value = {"label", "value", "type"})
public class AntdLeafNode extends LeafNode implements AntdNode {
    private final String label;
    private final String value;
    private final String type = "file";

    public AntdLeafNode(String label, String value) {
        this.label = label;
        this.value = value;
    }
}
