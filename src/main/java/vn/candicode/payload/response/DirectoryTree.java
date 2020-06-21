package vn.candicode.payload.response;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class DirectoryTree implements Serializable {
    private String challengeDir;
    private List<CCFile> children = new ArrayList<>();
}
