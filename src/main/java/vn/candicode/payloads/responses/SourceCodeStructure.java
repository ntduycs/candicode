package vn.candicode.payloads.responses;

import lombok.Getter;
import lombok.Setter;
import vn.candicode.common.structure.composite.impl.AntdNode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class SourceCodeStructure implements Serializable {
    private String root;
    private List<AntdNode> nodes = new ArrayList<>();
}
