package vn.candicode.common.structure.composite;

import lombok.Getter;

import java.util.List;

@Getter
public abstract class CompositeNode implements Node {
    protected List<Node> children;

    public void add(Node node) {
        children.add(node);
    }

    public void remove(Node node) {
        children.remove(node);
    }
}
