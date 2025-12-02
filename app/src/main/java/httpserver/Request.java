package httpserver;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import httpserver.parsers.RequestParser;

public class Request {

    private String method;
    private String resource;
    private String protocolVersion;
    private HashMap<String, String> headers;
    private String body;
    private boolean completed = false;

    public Request(String request) {
        this.headers = new HashMap<>();
        parse(request);
    }

    public String getMethod() {
        return method;
    }

    public String getResource() {
        return resource;
    }

    public String getProtocolVersion() {
        return protocolVersion;
    }

    public HashMap<String, String> getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }

    private void parse(String request) throws IllegalArgumentException {
        String[] parts = request.split("\r\n\r\n", 2);

        String[] head = parts[0].split("\r\n");
        this.body = parts.length > 1 ? parts[1] : "";

        Map<String, String> requestLine = RequestParser.requestLine(head[0]);
        this.method = requestLine.get("method");
        this.resource = requestLine.get("resource");
        this.protocolVersion = requestLine.get("protocol-version");

        String[] fieldLines = Arrays.copyOfRange(head, 1, head.length);
        this.headers = RequestParser.headers(fieldLines);

        if (headers.containsKey("content-lenght")) {
            int contentLenght = Integer.parseInt(headers.get("content-lenght"));
            if (body.length() != contentLenght) {
                throw new IllegalArgumentException("content-lenght and body mismatch lenght");
            }
        }

        this.headers = headers;
        this.completed = true;
    }

    public boolean isCompleted() {
        return this.completed;
    }
}
