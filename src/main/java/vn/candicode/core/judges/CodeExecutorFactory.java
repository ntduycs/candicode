package vn.candicode.core.judges;

import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

@Log4j2
public class CodeExecutorFactory {
    public static void main(String[] args) {
        int n = 0;

        try {
            ServerSocket server = new ServerSocket(2204);
            log.info("\n\nCandicode compilation server has been just started at port " + 2204);

            while (true) {
                n++;
                // Accept any incoming connection and serve it on a new thread
                Socket socket = server.accept();
                CodeExecutor executor = new CodeExecutor(socket, n);
                executor.start();
            }
        } catch (IOException e) {
            log.error("\n\nError when start code executor server \n" +
                "Error - {}, Message - {}", e.getClass().getSimpleName(), e.getMessage());
        }
    }
}
