import java.util.HashMap;
import java.util.Map;

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

    private void parse(String request) throws RuntimeException {
        String[] parts = request.split("\r\n\r\n");

        String[] head = parts[0].split("\r\n");
        this.body = parts.length > 1 ? parts[1] : "";

        Map<String, String> requestLine = RequestLine

        for (int i = 1; i < head.length; i++) {
            String[] fieldLine = head[i].split(": ", 2);
            String headerName = fieldLine[0];
            String headerValue = fieldLine[1];

            if (headerValue.isEmpty()) {
                throw new RuntimeException("header value is empty");
            }

            headers.put(
                    headerName.toLowerCase().trim(),
                    headerValue.trim());
        }

        if (headers.containsKey("content-lenght")) {
            int contentLenght = Integer.parseInt(headers.get("content-lenght"));
            if (body.length() != contentLenght) {
                throw new RuntimeException("content-lenght and body mismatch lenght");
            }
        }

        this.completed = true;
    }

    public boolean isCompleted() {
        return this.completed;
    }

    private static class RequestLine {

        public Map<String, String> parse(String line) {
            String[] requestLine = line.split(" ");

            int numberParts = 3;
            if (requestLine.length != numberParts) {
                throw new RuntimeException("request line is invalid");
            }

            String method = requestLine[0].trim();
            String resource = requestLine[1].trim();
            String protocolVersion = requestLine[2].trim();

            if (!method.matches("[A-Z]+")
                    || !resource.startsWith("/")
                    || !protocolVersion.matches("HTTP/[0-9]\\.[0-9]")) {
                throw new RuntimeException("invalid requestline syntaxis");
            }

            Map<String, String> items = new HashMap<>();
            items.put("method", method);
            items.put("resource", resource);
            items.put("protocol-version", protocolVersion);
            return items;
        }
    }
}
