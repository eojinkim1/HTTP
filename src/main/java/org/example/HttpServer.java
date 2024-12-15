package org.example;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HttpServer {
    private static final Logger logger = Logger.getLogger(HttpServer.class.getName());

    private final int port;

    public HttpServer(int port) {
        this.port = port;
    }

    @SuppressWarnings("all")
    public void run() {
        logger.info("HTTP 서버 Start! Port: " + port);
        while (true) {
            // try-catch-resouces 문법. Java 9
            try (ServerSocket server = new ServerSocket(port);
                 Socket socket = server.accept();
                 InputStream in = socket.getInputStream();
                 OutputStream out = socket.getOutputStream();
                 InputStreamReader reader = new InputStreamReader(in);
                 BufferedReader br = new BufferedReader(reader)) {

                //---
                // if
                // GET /login
                // 로그인 화면 반환


                // /login , GET, POST
                String firstHeader = br.readLine();
                System.out.println(firstHeader);

                if (firstHeader.contains("GET /login")) {
                    //로그인 화면
                    String body = """
                            <html>
                                <meta charset="UTF-8">
                                <h1>Login Form</h1>
                                <form method="POST" action="/login">
                                   id: <input name="id" />
                                   pw: <input name="pw" />
                                   <input type="submit" />
                                </form>
                            </html>
                                     """;
                    write(out, """
                            HTTP/1.1 200 OK
                            Content-Type: text/html; charset=utf-8
                            Content-Length: %d
                             
                            %s
                                    """.formatted(body.length(), body));
                }

                // if
                // POST /login
                // 로그인 처리
                if (firstHeader.contains("POST /login")) {
                    // /hello
                    // /hello?name=12

                    /**
                     * POST /login HTT{/1.1
                     * A: 123
                     * B: 123
                     * C: 123
                     *
                     * {"id":"123", "pw":"123"}
                     */

                    String line = br.readLine();
                    while (!line.isBlank()) {
                        line = br.readLine();
                    }
                    String body = br.readLine();
                    // System.out.println(body);
                    String[] lineSplit = body.split("&");
                    String id = lineSplit[0].substring(lineSplit[0].indexOf('=') + 1);
                    String pw = lineSplit[1].substring(lineSplit[1].indexOf('=') + 1);

                    System.out.println(id);
                    System.out.println(pw);

                    // SELECT user_id, password FROM users WHERE id = id

                    try (Connection con = DriverManager.getConnection("jdbc:h2:~/test;MODE=MySQL", "sa", "")) {
                        PreparedStatement selectTable = con.prepareStatement("""
                                SELECT password FROM users WHERE USER_ID = ?;
                                """);
                        selectTable.setString(1, id); // ? <-'?'값이 id라는 인자 값으로 바뀜
                        ResultSet resultSet = selectTable.executeQuery();
                        while (resultSet.next()){ //next() 줄 하나 씩 읽음 boolean 형이라 다음 줄이 있는 지 없는 지 확인함
                            String dbpw =resultSet.getString(1);

                            if (pw.equals(dbpw)){
                                System.out.println("로그인 성공!");
                            } else {
                                System.out.println("로그인 실패");
                            }
                        }
                        selectTable.close();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }


                    //id, pw 입력 받기
                    //DB에 id, pw 일치하는지 확인
                    logger.info("요청 응답 완료! HTTP 200 OK");
                }

                // if
                // GET /hello
                if (firstHeader.contains("GET /hello")) {
                    String[] lineSplit = firstHeader.split(" ");
                    String url = lineSplit[1];

                    int equalsIndex = url.indexOf('=');
                    String name = decodeString(url.substring(equalsIndex + 1));
                    logger.info("요청 접수! name: " + name);

                    String response = createResponse(name);
                    write(out, response);

                    logger.info("요청 응답 완료! 200");
                }
                //---
            } catch (IOException e) {
                logger.log(Level.FINER, e.getMessage());
                throw new RuntimeException(e);
            }
        }
    }

    private String decodeString(String encodedString) {
        return URLDecoder.decode(encodedString, StandardCharsets.UTF_8);
    }

    private String createResponse(String name) {
        String body = """
                 <html>
                    <meta charset="UTF-8">
                    <h1>Hello %s!</h1>
                 </html>
                """.formatted(name);

        return """
                 HTTP/1.1 200 OK
                 Content-Type: text/html; charset=utf-8
                 Content-Length: %d
                 
                 %s
                """.formatted(body.length(), body);
    }

    private void write(OutputStream out, String response) throws IOException {
        OutputStreamWriter writer = new OutputStreamWriter(out);
        BufferedWriter bufferedWriter = new BufferedWriter(writer);

        bufferedWriter.write(response);
        bufferedWriter.flush();
    }
}
