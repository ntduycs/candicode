package vn.candicode.common.structure.composite.impl;

import lombok.Getter;
import vn.candicode.common.structure.composite.CompositeNode;
import vn.candicode.common.structure.composite.Node;

import java.util.ArrayList;
import java.util.List;

@Getter
public class AntCompositeNode extends CompositeNode {
    private final String label;
    private final String value;
    private final String type = "directory";

    public AntCompositeNode(String label, String value) {
        this.label = label;
        this.value = value;
        this.children = new ArrayList<>();
    }

    public AntCompositeNode(String label, String value, List<Node> children) {
        this.label = label;
        this.value = value;
        this.children = children;
    }
}
