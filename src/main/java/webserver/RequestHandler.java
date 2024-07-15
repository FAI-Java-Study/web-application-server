package webserver;

import static util.HttpRequestUtils.*;
import static util.HttpRequestUtils.Pair.*;
import static util.IOUtils.*;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import model.User;

public class RequestHandler extends Thread {
	private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);
	private static final List<String> WEB_APP_ROUTE = Collections.unmodifiableList(
		Arrays.asList("/index.html", "/form.html", "/login.html"));

	private static final String CREATE_ROUTE = "/create?";

	private final Socket connection;

	public RequestHandler(Socket connectionSocket) {
		this.connection = connectionSocket;
	}

	public void run() {
		log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
			connection.getPort());

		try (InputStream in = connection.getInputStream();
			 OutputStream out = connection.getOutputStream();
			 BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			 DataOutputStream dos = new DataOutputStream(out)) {

			String line = reader.readLine();
			String requestedUrl = "";

			while (line != null && !"".equals(line)) {
				requestedUrl = getRequestedUrl(line);
				if (Objects.requireNonNull(requestedUrl).startsWith(CREATE_ROUTE)) {
					User user = createUser(requestedUrl);
					log.info("User: {}", user);
					break;
				}

				if (WEB_APP_ROUTE.contains(requestedUrl)) {
					break;
				}
				line = reader.readLine();
			}

			byte[] body = generateResponseBody(requestedUrl);
			response200Header(dos, body.length);
			responseBody(dos, body);
		} catch (IOException ioException) {
			log.error(ioException.getMessage());
		} catch (Exception exception) {
			log.error(exception.getMessage());
			throw new RuntimeException("알수없는 error", exception);
		}
	}

	/**
	 * 요청정보를 통해 유저 데이터를 생성한다
	 * @param requestedUrl
	 * @return
	 */
	private User createUser(String requestedUrl) {
		int index = requestedUrl.indexOf("?");
		String queryString = requestedUrl.substring(index + 1);
		Map<String, String> queryStringMap = parseQueryString(queryString);

		String userId = queryStringMap.get("userId");
		String password = queryStringMap.get("password");
		String name = queryStringMap.get("name");
		String email = queryStringMap.get("email");

		return new User(userId, password, name, email);
	}

	private byte[] generateResponseBody(String requestedUrl) {
		if (WEB_APP_ROUTE.contains(requestedUrl)) {
			try {
				return readFileAsByteArray(requestedUrl);
			} catch (IOException e) {
				log.error("I/O 에러 발생!!!: ", e);
			}
		}
		return "Hello World".getBytes();
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
