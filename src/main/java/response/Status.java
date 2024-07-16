package response;

public enum Status {
    OK("200", "OK"),
    FOUND("302", "FOUND");

    private String statusCode;
    private String statusMessage;

    Status(String statusCode, String statusMessage) {
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public String getStatusMessage() {
        return statusMessage;
    }
}
