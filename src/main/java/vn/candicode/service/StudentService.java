package vn.candicode.service;

import vn.candicode.payload.request.NewStudentRequest;

import java.io.IOException;

public interface StudentService {
    /**
     * Create new student account and init required student directories
     *
     * @see vn.candicode.payload.request.NewStudentRequest
     *
     * @param payload
     * @return id of new student
     */
    Long createAccount(NewStudentRequest payload) throws IOException;
}
