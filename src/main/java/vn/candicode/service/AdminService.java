package vn.candicode.service;

import vn.candicode.payload.request.NewAdminRequest;
import vn.candicode.payload.request.UpdateAdminRoleRequest;
import vn.candicode.security.UserPrincipal;

public interface AdminService {
    /**
     * @param payload
     * @param currentUser Only master admin can do this task
     * @return id of new admin
     */
    Long createAccount(NewAdminRequest payload, UserPrincipal currentUser);

    /**
     * @param payload
     * @param currentUser Only master admin can do this task
     */
    void updateRole(UpdateAdminRoleRequest payload, UserPrincipal currentUser);
}
