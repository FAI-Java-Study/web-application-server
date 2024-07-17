package webserver;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

import java.nio.file.Files;
import java.util.Map;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;

public class RequestHandler extends Thread {
	private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);
	
	private Socket connection;

	public RequestHandler(Socket connectionSocket) {
		this.connection = connectionSocket;
	}

	public void run() {
		log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(), connection.getPort());
		
		try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
			// TODO 사용자 요청에 대한 처리는 이 곳에 구현하면 된다.
			DataOutputStream dos = new DataOutputStream(out);

			BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));

			String line = br.readLine();

			byte[] body = "Hello World".getBytes();


			while (!"".equals(line) && line != null) {
				log.debug("header : {}", line);

				String[] tokens = line.split(" ");

				if (tokens.length > 2 && "/index.html".equals(tokens[1])) {
					body = Files.readAllBytes(new File("webapp" + tokens[1]).toPath());
				}

				if (tokens.length > 2 && tokens[1].contains("create")) {

					int index = tokens[1].indexOf("?");

					String requestPath = tokens[1].substring(0, index);
					String params = tokens[1].substring(index + 1);

					Map<String, String> stringStringMap = HttpRequestUtils.parseQueryString(params);

					String email = stringStringMap.get("email");
					String name = stringStringMap.get("name");
					String userId = stringStringMap.get("userId");
					String password = stringStringMap.get("password");

					log.debug("email : {}, name : {}, userId : {}, password : {}", email, name, userId, password);

					User user = new User(userId, password, name, email);

					log.debug("user : {}", user);
				}


				line = br.readLine();

				if (line == null) {
					return;
				}
			}

			response200Header(dos, body.length);
			responseBody(dos, body);
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}

	private void response200Header(DataOutputStream dos, int lengthOfBodyContent) {
		try {
			dos.writeBytes("HTTP/1.1 200 OK \r\n");
			dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
			dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
			dos.writeBytes("\r\n");
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}
	
	private void responseBody(DataOutputStream dos, byte[] body) {
		try {
			dos.write(body, 0, body.length);
			dos.writeBytes("\r\n");
			dos.flush();
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}
}
