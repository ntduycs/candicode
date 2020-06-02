package vn.candicode.common.structure.composite.impl;

import lombok.Getter;
import vn.candicode.common.structure.composite.LeafNode;

@Getter
public class AntLeafNode extends LeafNode {
    private final String label;
    private final String value;
    private final String type = "file";

    public AntLeafNode(String label, String value) {
        this.label = label;
        this.value = value;
    }
}
