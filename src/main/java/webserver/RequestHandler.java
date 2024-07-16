package webserver;

import controller.UrlMapper;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import request.HttpRequest;
import response.HttpHeaders;
import view.ViewResolver;

public class RequestHandler implements Runnable {

    private UrlMapper urlMapper;
    private ViewResolver viewResolver;
    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);

    private Socket connection;

    public RequestHandler(UrlMapper urlMapper, ViewResolver viewResolver, Socket connection) {
        this.urlMapper = urlMapper;
        this.connection = connection;
        this.viewResolver = viewResolver;
    }

    public void run() {
        logger.debug("New Client Connect! Connected IP : {}, Port : {}",
                connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));

            HttpRequest httpRequest = setupHttpRequest(br);

            String viewName = urlMapper.requestMapping(httpRequest);

            byte[] responseMessage = viewResolver.mapView(viewName);

            DataOutputStream dos = new DataOutputStream(out);
            response(dos, responseMessage);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private HttpRequest setupHttpRequest(BufferedReader br) throws IOException {
        //HTTP Request Line을 읽어옵니다.
        HttpRequest httpRequest = new HttpRequest(br.readLine());

        //HTTP Request Header를 읽어옵니다.
        String requestHeader;
        HttpHeaders httpRequestHeaders = new HttpHeaders();
        while (!(requestHeader = br.readLine()).equals("")) {
            //Request Header 객체에 삽입
            httpRequestHeaders.parse(requestHeader);
        }
        httpRequest.setHttpHeaders(httpRequestHeaders);

        //HTTP Request Body를 읽어옵니다.
        if (httpRequestHeaders.getContentLength() > 0) {
            char[] buffer = new char[httpRequestHeaders.getContentLength()];

            int byteRead = br.read(buffer, 0, httpRequestHeaders.getContentLength());
            //HttpRequest 객체에 Request Body 추가
            httpRequest.setBody(new String(buffer, 0, byteRead));
        }

        return httpRequest;
    }

    /**
     * Response 메시지를 전송합니다.
     * @param dos
     * @param body
     */
    private void response(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }
}
