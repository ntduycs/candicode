package vn.candicode.common.structure.composite.impl;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CCCategory implements CCNode {
    private String text;
    private List<CCCategory> children;
}
