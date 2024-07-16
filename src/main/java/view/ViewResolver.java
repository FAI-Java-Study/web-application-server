package view;

import java.io.IOException;
import java.util.Arrays;
import response.ContentsType;
import response.HttpHeaders;
import response.HttpResponse;
import response.Status;

public class ViewResolver {

    /**
     * View 이름을 인자로 받아 Response를 작성합니다.
     *
     * @throws IOException
     */

    public byte[] mapView(String viewName) throws IOException {
        //Redirect 키워드가 있는 경우 redirect 헤더로 작성
        if (viewName.startsWith("redirect:")) {
            String redirectViewName = viewName.replace("redirect:", "");

            View redirectView = new View(redirectViewName);
            HttpHeaders httpHeaders = new HttpHeaders("Location", redirectViewName);
            HttpResponse redirectResponse = new HttpResponse("HTTP/1.1", Status.FOUND, httpHeaders, redirectView);
            return redirectResponse.render();
        }

        //그 외의 경우 200 OK 응답을 반환
        ContentsType contentsType = mapTypeByIndentifier(viewName);
        HttpHeaders httpHeaders = new HttpHeaders("Content-Type", contentsType.getContentType());
        View okView = new View(viewName, contentsType);
        HttpResponse okResponse = new HttpResponse("HTTP/1.1", Status.OK, httpHeaders, okView);

        return okResponse.render();
    }

    /**
     * viewName의 파일 확장자를 기준으로 일치하는 content-type을 반환합니다.
     *
     * @param view
     * @return
     * @throws IOException
     */
    private ContentsType mapTypeByIndentifier(String view) {

        return Arrays.stream(ContentsType.values())
            .filter(contentsType -> view.matches(contentsType.getIdentifier()))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Match되는 Contents-Type이 존재하지 않습니다."));
    }

}
