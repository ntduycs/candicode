package vn.candicode.service;

import vn.candicode.payload.request.NewStudentRequest;
import vn.candicode.payload.request.UpdateStudentRoleRequest;
import vn.candicode.payload.response.UserProfile;
import vn.candicode.security.UserPrincipal;

import java.io.IOException;

public interface StudentService {
    /**
     * Create new student account and init required student directories
     *
     * @param payload
     * @return id of new student
     * @see vn.candicode.payload.request.NewStudentRequest
     */
    Long createAccount(NewStudentRequest payload) throws IOException;

    /**
     * Update roles for student when he has just upgrade/downgrade his package (plan)
     *
     * @param payload
     */
    void updateRole(Long studentId, UpdateStudentRoleRequest payload, UserPrincipal me);

    UserProfile getStudentProfile(Long studentId);
}
