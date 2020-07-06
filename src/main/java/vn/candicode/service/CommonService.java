package vn.candicode.service;

import org.springframework.stereotype.Component;
import vn.candicode.entity.CategoryEntity;
import vn.candicode.entity.LanguageEntity;
import vn.candicode.entity.RoleEntity;
import vn.candicode.entity.StudentPlanEntity;
import vn.candicode.repository.CategoryRepository;
import vn.candicode.repository.LanguageRepository;
import vn.candicode.repository.RoleRepository;
import vn.candicode.repository.StudentPlanRepository;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class CommonService {

    private final Map<Role, RoleEntity> adminRoles = new HashMap<>();
    private final Map<Role, RoleEntity> studentRoles = new HashMap<>();

    private final Map<String, CategoryEntity> categories;
    private final Map<String, LanguageEntity> languages;
    private final Map<String, StudentPlanEntity> plans;

    public CommonService(RoleRepository roleRepository, CategoryRepository categoryRepository, LanguageRepository languageRepository, StudentPlanRepository studentPlanRepository) {
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
        this.plans = studentPlanRepository.findAll().stream().collect(Collectors.toMap(StudentPlanEntity::getName, plan -> plan));
    }

    enum Role {
        STUDENT(1, "student", false, true),
        CHALLENGE_CREATOR(2, "challenge creator", true, true),
        CONTEST_CREATOR(4, "contest creator", true, true),
        TUTORIAL_CREATOR(3, "tutorial creator", true, true),
        ADMIN(5, "admin", true, false),
        SUPER_ADMIN(6, "super admin", true, false),
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

    public Map<String, StudentPlanEntity> getPlans() {
        return plans;
    }
}
