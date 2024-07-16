package util;

public enum HttpMethod {
    GET("GET"),
    POST("POST");

    private String method;

    HttpMethod(String method) {
        this.method = method;
    }

    public boolean isPost() {
        return this == POST;
    }
}
