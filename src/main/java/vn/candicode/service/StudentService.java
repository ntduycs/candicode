package vn.candicode.service;

import vn.candicode.payload.request.NewStudentRequest;

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
    void updateRole(UpdateStudentRoleRequest payload);
}
