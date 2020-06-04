package vn.candicode.common.structure.composite.impl;

import lombok.Getter;
import vn.candicode.common.structure.composite.LeafNode;

@Getter
public class AntdLeafNode extends LeafNode implements AntdNode {
    private final String label;
    private final String value;
    private final String type = "file";

    public AntdLeafNode(String label, String value) {
        this.label = label;
        this.value = value;
    }
}
