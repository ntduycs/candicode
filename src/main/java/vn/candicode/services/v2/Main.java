package vn.candicode.services.v2;

import org.springframework.util.StringUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class Main {
    private static final String ROOT = System.getProperty("user.home") + "/Desktop/Candicode_v2";

    public static void main(String[] args) {
        List<String> inputs = List.of("[1,2]", "[1,2,3]", "[1,0     ");
        List<String> outputs = List.of("[0.5]", "[0.5, 0.67]", "[0.5, Infinity]");

        ProcessBuilder processBuilder = new ProcessBuilder("javac", "javacc/Main.java");

        processBuilder.directory(new File(ROOT));

        try {
            Process process = processBuilder.start();
            process.waitFor();

            String compileError = new String(process.getErrorStream().readAllBytes(), StandardCharsets.UTF_8);

            System.out.println("Compile Error: " + (StringUtils.hasText(compileError) ? compileError : "No error. Compile successfully"));

            if (!StringUtils.hasText(compileError)) {
                process = processBuilder.command("java", "javacc/Main").start();

                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream(), StandardCharsets.UTF_8));

                for (String input : inputs) {
                    writer.write(input);
                    writer.newLine();
                }

                writer.close();

                process.waitFor();

                if (process.isAlive()) {
                    process.destroyForcibly();
                }

                String actualOutput;
                int testcaseNo = 1;
                BufferedReader outputReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                while ((actualOutput = outputReader.readLine()) != null) {
                    System.out.println(String.format("Testcase %d: Expected: %s, Actual: %s", testcaseNo, outputs.get(testcaseNo - 1), actualOutput));
                    testcaseNo++;
                }

                process.waitFor();
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
