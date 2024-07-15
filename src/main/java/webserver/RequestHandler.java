package webserver;

import static util.HttpRequestUtils.Pair.*;
import static util.IOUtils.*;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestHandler extends Thread {
	private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);
	private static final String INDEX_HTML = "/index.html";

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

				if (INDEX_HTML.equals(requestedUrl)) {
					break;
				}
				line = reader.readLine();
			}

			byte[] body = generateResponseBody(requestedUrl);
			response200Header(dos, body.length);
			responseBody(dos, body);
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}

	private byte[] generateResponseBody(String requestedUrl) {
		if (INDEX_HTML.equals(requestedUrl)) {
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
