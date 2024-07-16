package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class IOUtils {

	private static final String WEP_APP_PATH = "./webapp";

	/**
	 * @param bufferedReader - Request Body를 시작하는 시점이어야 한다.
	 * @param contentLength - Request Header의 Content-Length 값이다.
	 * @return
	 * @throws IOException
	 */
	public static String readData(BufferedReader bufferedReader, int contentLength) throws IOException {
		char[] body = new char[contentLength];
		bufferedReader.read(body, 0, contentLength);
		return URLDecoder.decode(String.copyValueOf(body), StandardCharsets.UTF_8.name());
	}

	public static byte[] readFileAsByteArray(String url) throws IOException {
		return Files.readAllBytes(new File(WEP_APP_PATH + url).toPath());
	}
}
