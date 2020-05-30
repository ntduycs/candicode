package vn.candicode.core.judges;

import lombok.extern.log4j.Log4j2;
import vn.candicode.core.compilers.*;

import java.io.*;
import java.net.Socket;
import java.util.Map;

@Log4j2
public class CodeExecutor extends Thread {
    Socket socket;
    int requestNumber;
    File stagingDirectory;

    /**
     * Allocates a new {@code Thread} object. Automatically generated names are of the form
     * {@code "Thread-"+}<i>n</i>, where <i>n</i> is an integer.
     */
    public CodeExecutor(Socket socket, int requestNumber) {
        this.socket = socket;
        this.requestNumber = requestNumber;
        this.stagingDirectory = new File("stage/" + requestNumber);
    }

    /**
     * If this thread was constructed using a separate
     * {@code Runnable} run object, then that
     * {@code Runnable} object's {@code run} method is called;
     * otherwise, this method does nothing and returns.
     * <p>
     * Subclasses of {@code Thread} should override this method.
     *
     * @see #start()
     */
    @Override
    public void run() {
        stagingDirectory.mkdirs();

        try {
            log.info("\n\nConstructing input stream reader...");
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            log.info("\n\nConstructing output stream writer...");
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            log.info("\n\nReading data from input stream reader...");
            Map<String, String> inputContainer = readInputFromSocket(in);

            log.info("\n\nCreating input file...");

            createInputFile(inputContainer.get("input"));

            log.info("\n\nCompiling file " + inputContainer.get("file") + "...\n");

            LanguageCompiler compiler = null;

            switch (inputContainer.get("language")) {
                case "java":
                    compiler = new JavaCompiler(
                        inputContainer.get("file"),
                        inputContainer.get("content"),
                        stagingDirectory.getAbsolutePath(),
                        Integer.parseInt(inputContainer.get("timeout"))
                    );
                    break;
                case "c":
                    compiler = new CCompiler(
                        inputContainer.get("file"),
                        inputContainer.get("content"),
                        stagingDirectory.getAbsolutePath(),
                        Integer.parseInt(inputContainer.get("timeout"))
                    );
                    break;
                case "cpp":
                    compiler = new CppCompiler(
                        inputContainer.get("file"),
                        inputContainer.get("content"),
                        stagingDirectory.getAbsolutePath(),
                        Integer.parseInt(inputContainer.get("timeout"))
                    );
                    break;
                case "python":
                    compiler = new PythonCompiler(
                        inputContainer.get("file"),
                        inputContainer.get("content"),
                        stagingDirectory.getAbsolutePath(),
                        Integer.parseInt(inputContainer.get("timeout"))
                    );
                    break;
                default:
            }

            assert compiler != null;
            compiler.compile();

            String errors = readCompileErrors();

            if (!"".equals(errors)) {
                out.println("0");
                out.println(errors);
            } else {
                compiler.execute();

                if (compiler.isTimedout()) {
                    out.println("2");
                } else {
                    out.println("1");
                    out.println(readExecutionOutput());
                }
            }

            socket.close();
        } catch (FileNotFoundException e) {
            log.error("File not found. Message - {}", e.getMessage());
        } catch (IOException e) {
            log.error("IO error. Message - {}", e.getMessage());
        }
    }

    private String readExecutionOutput() {
        String line;
        StringBuilder content = new StringBuilder();

        log.info("\n\nReading execution output...");

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(stagingDirectory.getAbsolutePath() + "/out.txt")));
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        } catch (FileNotFoundException e) {
            log.warn("File not found. Message - {}", e.getMessage());
        } catch (IOException e) {
            log.warn("IO error. Message - {}", e.getMessage());
        }

        return content.toString().trim();
    }

    private String readCompileErrors() {
        String line;
        StringBuilder content = new StringBuilder();

        log.info("\n\nReading compile errors...");

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(stagingDirectory.getAbsolutePath() + "/err.txt")));
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        } catch (FileNotFoundException e) {
            log.warn("File not found. Message - {}", e.getMessage());
        } catch (IOException e) {
            log.warn("IO error. Message - {}", e.getMessage());
        }

        return content.toString().trim();
    }

    // FIXME: Most critical fix
    private Map<String, String> readInputFromSocket(BufferedReader reader) throws IOException {
        return Map.of(
            "file", reader.readLine().replace("something", "\n"),
            "input", reader.readLine().replace("something", "\n"),
            "language", reader.readLine().replace("something", "\n"),
            "content", reader.readLine().replace("something", "\n"),
            "timeout", reader.readLine()
        );
    }

    private void createInputFile(String input) throws FileNotFoundException {
        PrintWriter writer = new PrintWriter(new FileOutputStream("stage/" + requestNumber + "/in.txt"));
        writer.println(input);
        writer.close();
    }
}
