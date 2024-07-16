package response;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import view.View;

/**
 * 이 클래스는 Response에 대한 메타 데이터를 저장합니다.
 */
public class HttpResponse {

    String httpVersion;
    Status status;
    HttpHeaders httpHeaders;
    View view;

    public HttpResponse(String httpVersion, Status status, HttpHeaders httpHeaders, View view) {
        this.httpVersion = httpVersion;
        this.status = status;
        this.httpHeaders = httpHeaders;
        this.view = view;
    }

    public void setContentLength(int contentLength) {
        httpHeaders.put("Content-Length", String.valueOf(contentLength));
    }

    public byte[] getResponseLine() {
        String headLine = String.join(" ", httpVersion, status.getStatusCode(), status.getStatusMessage());
        return (headLine + "\r\n" + httpHeaders).getBytes();
    }

    public byte[] render() throws IOException {
        byte[] headers = getResponseLine();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        // Body가 있는 경우
        outputStream.write(headers);
        if (view.hasBody()) {
            byte[] body = Files.readAllBytes(new File(view.getPath()).toPath());
            setContentLength(body.length);
            outputStream.write(body);
        }

        return outputStream.toByteArray();
    }


}
