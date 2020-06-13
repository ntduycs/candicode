package vn.candicode.services.v1;

import vn.candicode.payloads.requests.StudentRequest;

public interface StudentService {
    /**
     * Create new student account and init required student directories
     *
     * @param payload
     * @return Newly created student id
     */
    Long createStudentAccount(StudentRequest payload);
}
