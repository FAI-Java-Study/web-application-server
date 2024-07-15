package util;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static util.HttpRequestUtils.Pair.*;

import java.util.Map;

import org.junit.Test;

import util.HttpRequestUtils.Pair;

public class HttpRequestUtilsTest {
	@Test
	public void parseQueryString() {
		String queryString = "userId=javajigi";
		Map<String, String> parameters = HttpRequestUtils.parseQueryString(queryString);
		assertThat(parameters.get("userId"), is("javajigi"));
		assertThat(parameters.get("password"), is(nullValue()));
		
		queryString = "userId=javajigi&password=password2";
		parameters = HttpRequestUtils.parseQueryString(queryString);
		assertThat(parameters.get("userId"), is("javajigi"));
		assertThat(parameters.get("password"), is("password2"));
	}
	
	@Test
	public void parseQueryString_null() {
		Map<String, String> parameters = HttpRequestUtils.parseQueryString(null);
		assertThat(parameters.isEmpty(), is(true));
		
		parameters = HttpRequestUtils.parseQueryString("");
		assertThat(parameters.isEmpty(), is(true));
		
		parameters = HttpRequestUtils.parseQueryString(" ");
		assertThat(parameters.isEmpty(), is(true));
	}
	
	@Test
	public void parseQueryString_invalid() {
		String queryString = "userId=javajigi&password";
		Map<String, String> parameters = HttpRequestUtils.parseQueryString(queryString);
		assertThat(parameters.get("userId"), is("javajigi"));
		assertThat(parameters.get("password"), is(nullValue()));
	}
	
	@Test
	public void getKeyValue() throws Exception {
		Pair pair = HttpRequestUtils.getKeyValue("userId=javajigi", "=");
		assertThat(pair, is(new Pair("userId", "javajigi")));
	}
	
	@Test
	public void getKeyValue_invalid() throws Exception {
		Pair pair = HttpRequestUtils.getKeyValue("userId", "=");
		assertThat(pair, is(nullValue()));
	}
	
	@Test
	public void parseHeader() throws Exception {
		String header = "Content-Length: 59";
		Pair pair = HttpRequestUtils.parseHeader(header);
		assertThat(pair, is(new Pair("Content-Length", "59")));
	}

	@Test
	public void testGetRequestedUrl() {
		// 정상적인 HTTP 요청 라인
		assertEquals("/index.html", getRequestedUrl("GET /index.html HTTP/1.1"));
		assertEquals("/", getRequestedUrl("POST / HTTP/1.1"));
		assertEquals("/api/users?id=123", getRequestedUrl("GET /api/users?id=123 HTTP/1.1"));

		// 비정상적인 입력
		assertEquals("", getRequestedUrl(""));
		assertEquals("", getRequestedUrl(null));
		assertEquals("", getRequestedUrl("   "));
		assertEquals("", getRequestedUrl("GET"));
		assertEquals("", getRequestedUrl("GET "));
	}
}
