package vn.candicode.util;

import vn.candicode.common.FileStorageType;
import vn.candicode.core.StorageService;
import vn.candicode.entity.StudentEntity;
import vn.candicode.entity.UserEntity;
import vn.candicode.payload.response.UserDetails;
import vn.candicode.payload.response.UserSummary;

import java.util.stream.Collectors;

public class UserBeanUtils {
    private static StorageService storageService;

    public static UserSummary summarize(UserEntity student) {
        UserSummary summary = new UserSummary();

        summary.setAvatar(storageService.resolvePath(student.getAvatar(), FileStorageType.AVATAR, student.getUserId()));
        summary.setEmail(student.getEmail());
        summary.setFirstName(student.getFirstName());
        summary.setLastName(student.getLastName());
        summary.setUserId(student.getUserId());
        summary.setType("admin");
        if (student instanceof StudentEntity) {
            summary.setPlan(((StudentEntity) student).getStudentPlan().getName());
            summary.setType("student");
        }
        summary.setRoles(student.getRoles().stream().map(e -> e.getRole().getName()).collect(Collectors.toList()));

        return summary;
    }

    public static UserDetails details(StudentEntity student) {
        UserDetails details = new UserDetails();

        details.setAvatar(storageService.resolvePath(student.getAvatar(), FileStorageType.AVATAR, student.getUserId()));
        details.setEmail(student.getEmail());
        details.setFirstName(student.getFirstName());
        details.setLastName(student.getLastName());
        details.setUserId(student.getUserId());
        details.setPlan(student.getStudentPlan().getName());
        details.setRoles(student.getRoles().stream().map(e -> e.getRole().getName()).collect(Collectors.toList()));

        return details;
    }

    public static void setStorageService(StorageService service) {
        storageService = service;
    }
}
