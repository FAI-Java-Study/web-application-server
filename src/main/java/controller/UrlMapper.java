package controller;

import request.HttpRequest;

public class UrlMapper {
    private UserController userController;

    public UrlMapper(UserController userController) {
        this.userController = userController;
    }

    /**
     * HTTP Request를 받아 요청 URL을 분석해 비즈니스 로직을 수행하고 뷰를 String으로 반환합니다.
     * @param httpRequest
     * @return view
     */
    public String requestMapping(HttpRequest httpRequest) {
        if (httpRequest.getUrl().equals("/")) {
            httpRequest.setUrl("/index.html");
            return httpRequest.getUrl();
        }

        if (httpRequest.getUrl().equals("/create") && httpRequest.getMethod().isPost()) {
            return userController.userJoin(httpRequest);
        }

        if (httpRequest.getUrl().equals("/login") && httpRequest.getMethod().isPost()) {
            return userController.userLogin(httpRequest);
        }

        return httpRequest.getUrl();
    }
}
