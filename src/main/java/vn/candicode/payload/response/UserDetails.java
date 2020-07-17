package vn.candicode.payload.response;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class UserDetails implements Serializable {
    private Long userId;
    private String email;
    private String firstName;
    private String lastName;
    private String avatar;
    private String plan;
    private List<String> roles = new ArrayList<>();
}
