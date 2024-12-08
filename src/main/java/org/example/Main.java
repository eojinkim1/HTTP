package org.example;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

/**
 * 사전 지식 (알고 있다 가정하는 것들)
 * 1. TCP/IP
 *  1.1 TCP/IP 핸드셰이킹 과정, 연결 종료 과정
 *  1.2 OSI 7 Layer / 4계층
 * 2. HTTP
 *  2.1 OSI 7 Layer / 7계층
 */

/**
 POST /create-developer HTTP/1.1
 Content-Type: application/json
 Accept: application/json

 {
 "developerLevel": "JUNIOR",
 "developerSkillType": "FULL_STACK",
 "experienceYears": 2,
 "memberId": "sunny.flower",
 "name": "sun",
 "age": 36
 }
 */
public class Main {
    private static final Logger logger = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {
        //1. HTTP Get으로 요청 받는다.
        //2. 이 때 요청 파라미터로 이름(name)을 입력 받는다.
        //3. 응답으로 html, Hello {name}!을 응답한다.
        try (ServerSocket server = new ServerSocket(8080)) {
            logger.info("HTTP 서버 Start! Port: "+8080);
            // 클라이언트 요청이 올때까지 blocking!
            Socket socket = server.accept();

            InputStream in = socket.getInputStream();

            InputStreamReader reader = new InputStreamReader(in);
            BufferedReader br = new BufferedReader(reader);

            String line = br.readLine();

            String[] name = line.split(" ");

            name[1].substring(name[1].indexOf('=') + 1);
            String realName = name[1].substring(name[1].indexOf('=') + 1);
            realName = URLDecoder.decode(realName, StandardCharsets.UTF_8.toString());

            logger.info("요청 접수! name: "+realName);

            OutputStream out = socket.getOutputStream();

            String body = """
                     <html>
                        <meta charset="UTF-8">
                        <h1>Hello %s!</h1>
                     </html>
                    """.formatted(realName);

            out.write("""
                     HTTP/1.1 200 OK
                     Content-Type: text/html; charset=utf-8
                     Content-Length: %d
                     
                     %s
                    """.formatted(body.length(), body).getBytes(StandardCharsets.UTF_8));
            out.flush();

            logger.info("요청 응답 완료! 200");

            in.close();
            out.close();
            socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}