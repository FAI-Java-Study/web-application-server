package webserver;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.nio.file.Files;
import java.util.Map;
import repository.DataBase;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;
import util.IOUtils;

public class RequestHandler extends Thread {

    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private Socket connection;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
            connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            DataOutputStream dos = new DataOutputStream(out);
            BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));

            String line = br.readLine();
            if (line == null) {
                return;
            }

            String[] tokens = line.split(" ");
            if (tokens.length < 2) {
                return;
            }

            String requestPath = tokens[1];
            byte[] body = "Hello World".getBytes();


            if (requestPath.startsWith("/user/create") && "GET".equals(tokens[0])) {

                String splitParams = requestPath.split("\\?")[1];

                Map<String, String> params = HttpRequestUtils.parseQueryString(splitParams);
                User user = new User(params.get("userId"), params.get("password"), params.get("name"), params.get("email"));
                log.debug("User : {}", user);
            }

            if (requestPath.contains(".html")) {
                body = Files.readAllBytes(new File("webapp" + requestPath).toPath());

            }

            if (requestPath.startsWith("/user/create") && "POST".equals(tokens[0])) {
                int contentLength = 0;
                while (!"".equals(line)) {
                    line = br.readLine();
                    if (line.contains("Content-Length")) {
                        contentLength = Integer.parseInt(line.split(":")[1].trim());
                    }
                }
                String bodyStr = IOUtils.readData(br, contentLength);
                log.debug("Request Body : {}", bodyStr);

                Map<String, String> params = HttpRequestUtils.parseQueryString(bodyStr);

                User user = new User(params.get("userId"), params.get("password"), params.get("name"), params.get("email"));

                log.debug("User : {}", user);

                DataBase.addUser(user);

                response302Header(dos, "/index.html");
            }

            if (requestPath.startsWith("/user/login") && "POST".equals(tokens[0])) {
                int contentLength = 0;
                while (!"".equals(line)) {
                    line = br.readLine();
                    if (line.contains("Content-Length")) {
                        contentLength = Integer.parseInt(line.split(":")[1].trim());
                    }
                }
                String bodyStr = IOUtils.readData(br, contentLength);
                log.debug("Request Body : {}", bodyStr);

                Map<String, String> params = HttpRequestUtils.parseQueryString(bodyStr);

                User user = DataBase.findUserById(params.get("userId")).orElseThrow(() -> new Exception("User Not Found"));

                if (user.getPassword().equals(params.get("password"))) {
                    log.debug("Login Success");
                    response302Header(dos, "/index.html");
                    setHeaderCookie(dos, "logined=true");
                } else {
                    log.debug("Login Fail");
                }
            }

            if (requestPath.startsWith("/user/list") && "GET".equals(tokens[0]) && "true".equals(HttpRequestUtils.parseCookies(br.readLine()).get("logined"))) {
                StringBuilder sb = new StringBuilder();
                sb.append("<table border='1'>");
                sb.append("<tr>");
                sb.append("<th>userId</th>");
                sb.append("<th>name</th>");
                sb.append("<th>email</th>");
                sb.append("</tr>");
                for (User user : DataBase.findAll()) {
                    sb.append("<tr>");
                    sb.append("<td>" + user.getUserId() + "</td>");
                    sb.append("<td>" + user.getName() + "</td>");
                    sb.append("<td>" + user.getEmail() + "</td>");
                    sb.append("</tr>");
                }
                sb.append("</table>");
                body = sb.toString().getBytes();
            }

            if (requestPath.startsWith("/user/list") && "GET".equals(tokens[0]) && !"true".equals(HttpRequestUtils.parseCookies(br.readLine()).get("logined"))) {
                response302Header(dos, "/login.html");
            }

        while ((line = br.readLine()) != null && !"".equals(line)) {
                log.debug("header : {}", line);
            }

            response200Header(dos, body.length);
            responseBody(dos, body);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void setHeaderCookie(DataOutputStream dos, String cookie) {
        try {
            dos.writeBytes("Set-Cookie: " + cookie + "\r\n");
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void response302Header(DataOutputStream dos, String location) {
        try {
            dos.writeBytes("HTTP/1.1 302 Redirect \r\n");
            dos.writeBytes("Location: " + location + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.writeBytes("\r\n");
            dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }
}
