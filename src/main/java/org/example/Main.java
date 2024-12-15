package org.example;

/**
 * 사전 지식 (알고 있다 가정하는 것들)
 * 1. TCP/IP
 *  1.1 TCP/IP 핸드셰이킹 과정, 연결 종료 과정
 *  1.2 OSI 7 Layer / 4계층
 * 2. HTTP
 *  2.1 OSI 7 Layer / 7계층
 */

import org.h2.tools.Server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

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
    public static void main(String[] args) {
        try {
            Server.createWebServer("-web", "-webAllowOthers", "-webPort", "8082").start();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
//        연습용_디비_준비();

        int port = 8080;
        HttpServer httpServer = new HttpServer(port);
        httpServer.run();

        //로그인
        // 1. POST 요청으로 id, pw 받기
        // 2. DB에 id, pw 일치하는지 확인
        // 3. 로그인 성공했다면 HTTP 200 OK, 실패했다면 HTTP 400 BAD_REQUEST
        //OTHER...
        // 1. 로그인 화면도 있으면 좋음.
    }

    private static void 연습용_디비_준비() {
        try (Connection con = DriverManager.getConnection("jdbc:h2:~/test;MODE=MySQL", "sa", "")) {
            PreparedStatement createTable = con.prepareStatement("""
                     CREATE TABLE users(
                         id bigint auto_increment primary key,
                         user_id varchar(500) not null unique,
                         password varchar(500) not null
                     );
                     """);
            createTable.execute();
            createTable.close();

            PreparedStatement insertTestData = con.prepareStatement("""
                    INSERT INTO users(user_id, password)
                    VALUES('rlarjs', '1234'),
                          ('eojin', '1234');
                    """);
            insertTestData.execute();
            insertTestData.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}