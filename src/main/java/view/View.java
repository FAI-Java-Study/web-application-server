package view;

import java.io.IOException;
import response.ContentsType;

public class View {

    private String viewName;
    private ContentsType contentsType;

    public View(String viewName, ContentsType contentsType) {
        this.viewName = viewName;
        this.contentsType = contentsType;
    }

    public View(String viewName) {
        this.viewName = viewName;
    }

    /**
     * httpResponse에서 header 정보를 읽어와 첫 줄과 header 메시지를 작성하고,
     * viewName으로 파일 경로에 있는 파일의 Body와 합쳐 반환한다.
     * @return httpResponseMessage
     * @throws IOException
     */

    public String getPath() {
        return "webapp" + viewName;
    }

    public boolean hasBody() {
        return contentsType != null ? true : false;
    }
}
