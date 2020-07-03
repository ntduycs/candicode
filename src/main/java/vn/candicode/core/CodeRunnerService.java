package vn.candicode.core;

import java.io.File;

/**
 * Potential Improvements:
 * <ul>
 *     <li>Find a way to get more effectively elapsed time by process</li>
 *     <li>Find a way to get effective consumed memory</li>
 * </ul>
 */
public interface CodeRunnerService {
    CompileResult compile(File root, String language);

    /**
     * @param root
     * @param clock    set to 3s if 0 <= clock <= 3,000,000,000 (nanoseconds)
     * @param language
     * @return
     */
    ExecutionResult run(File root, long clock, String language);

    void cleanGarbageFiles(File root, String lang);
}
