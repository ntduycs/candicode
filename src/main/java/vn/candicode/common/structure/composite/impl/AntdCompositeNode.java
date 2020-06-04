package vn.candicode.common.structure.composite.impl;

import lombok.Getter;
import vn.candicode.common.structure.composite.CompositeNode;
import vn.candicode.common.structure.composite.Node;

import java.util.ArrayList;
import java.util.List;

@Getter
public class AntdCompositeNode extends CompositeNode implements AntdNode {
    private final String label;
    private final String value;
    private final String type = "directory";

    public AntdCompositeNode(String label, String value) {
        this.label = label;
        this.value = value;
        this.children = new ArrayList<>();
    }

    public AntdCompositeNode(String label, String value, List<Node> children) {
        this.label = label;
        this.value = value;
        this.children = children;
    }
}
