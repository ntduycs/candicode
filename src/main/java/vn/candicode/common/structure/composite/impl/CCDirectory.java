package vn.candicode.common.structure.composite.impl;

import lombok.Getter;
import vn.candicode.common.structure.composite.CompositeNode;

@Getter
public class CCDirectory extends CompositeNode implements CCNode {
    private final String name;
    private final String path;

    public CCDirectory(String name, String path) {
        this.name = name;
        this.path = path;
    }
}
