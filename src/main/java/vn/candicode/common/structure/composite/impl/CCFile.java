package vn.candicode.common.structure.composite.impl;

import lombok.Getter;
import lombok.Setter;
import vn.candicode.common.structure.composite.LeafNode;

@Getter
@Setter
public class CCFile extends LeafNode implements CCNode {
    private final String name;
    private final String path;

    public CCFile(String name, String path) {
        this.name = name;
        this.path = path;
    }
}
