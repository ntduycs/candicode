package vn.candicode.common.structure.adapter;

import vn.candicode.common.structure.composite.Node;
import vn.candicode.common.structure.composite.impl.*;

import java.util.ArrayList;
import java.util.List;

public class AntdAdapter {
    public static List<AntdNode> fromNodes(List<Node> nodes) {
        List<AntdNode> antdNodes = new ArrayList<>();
        for (Node node : nodes) {
            if (node instanceof CCDirectory) {
                CCDirectory directory = (CCDirectory) node;
                AntdCompositeNode antdNode = new AntdCompositeNode(directory.getName(), directory.getPath());
                antdNode.getChildren().addAll(fromNodes(directory.getChildren()));
                antdNodes.add(antdNode);
            } else if (node instanceof CCFile) {
                CCFile file = (CCFile) node;
                AntdLeafNode antdNode = new AntdLeafNode(file.getName(), file.getPath());
                antdNodes.add(antdNode);
            }
        }
        return antdNodes;
    }
}
