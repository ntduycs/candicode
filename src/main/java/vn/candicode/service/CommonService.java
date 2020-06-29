package vn.candicode.service;

import org.springframework.stereotype.Component;
import vn.candicode.entity.CategoryEntity;
import vn.candicode.entity.LanguageEntity;
import vn.candicode.entity.RoleEntity;
import vn.candicode.repository.CategoryRepository;
import vn.candicode.repository.RoleRepository;
import vn.candicode.security.LanguageRepository;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class CommonService {

    private final Map<Role, RoleEntity> adminRoles = new HashMap<>();
    private final Map<Role, RoleEntity> studentRoles = new HashMap<>();

    private final Map<String, CategoryEntity> categories;
    private final Map<String, LanguageEntity> languages;

    public CommonService(RoleRepository roleRepository, CategoryRepository categoryRepository, LanguageRepository languageRepository) {
        List<RoleEntity> roleEntities = roleRepository.findAll();

        for (RoleEntity roleEntity : roleEntities) {
            Optional<Role> role = Role.getByRoleId(roleEntity.getRoleId());
            if (role.isPresent()) {
                if (role.get().isAdminRole()) adminRoles.put(role.get(), roleEntity);
                if (role.get().isStudentRole()) studentRoles.put(role.get(), roleEntity);
            }
        }

        this.categories = categoryRepository.findAll().stream().collect(Collectors.toMap(CategoryEntity::getName, cate -> cate));
        this.languages = languageRepository.findAll().stream().collect(Collectors.toMap(LanguageEntity::getName, lang -> lang));
    }

    enum Role {
        STUDENT(1, "student", false, true),
        ADMIN(2, "admin", true, false),
        PARTNER(3, "partner", false, false),
        CHALLENGE_CREATOR(4, "challenge creator", true, true),
        CONTEST_CREATOR(5, "contest creator", true, true),
        TUTORIAL_CREATOR(6, "tutorial creator", true, true),
        SUPER_ADMIN(7, "super admin", true, false),
        MANAGE_ADMIN(8, "manage admin", true, false),
        CONTENT_ADMIN(9, "content admin", true, false),
        ;

        private final long roleId;
        private final String roleName;
        private final boolean adminRole;
        private final boolean studentRole;

        Role(long roleId, String roleName, boolean adminRole, boolean studentRole) {
            this.roleId = roleId;
            this.roleName = roleName;
            this.adminRole = adminRole;
            this.studentRole = studentRole;
        }

        public long getRoleId() {
            return roleId;
        }

        public String getRoleName() {
            return roleName;
        }

        public boolean isAdminRole() {
            return adminRole;
        }

        public boolean isStudentRole() {
            return studentRole;
        }

        static Optional<Role> getByRoleId(Long id) {
            return Arrays.stream(Role.values()).filter(item -> item.getRoleId() == id).findFirst();
        }
    }

    public Map<Role, RoleEntity> getAdminRoles() {
        return adminRoles;
    }

    public Map<Role, RoleEntity> getStudentRoles() {
        return studentRoles;
    }

    public Map<String, CategoryEntity> getCategories() {
        return categories;
    }

    public Map<String, LanguageEntity> getLanguages() {
        return languages;
    }
}
