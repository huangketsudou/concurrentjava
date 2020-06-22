package com.jiedong.taskexecution;

import java.io.IOException;
import java.net.*;

/**
 * @author 19411
 * @date 2020/06/22 17:54
 **/
public class SingleThreadWebServer {
    public static void main(String[] args) throws IOException {
        ServerSocket socket = new ServerSocket(80);
        while (true) {
            Socket connection = socket.accept();
            handleRequest(connection);
        }
    }

    private static void handleRequest(Socket connection) {
        // request-handling logic here
    }
}
