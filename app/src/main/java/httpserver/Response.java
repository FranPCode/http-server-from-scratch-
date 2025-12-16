package httpserver;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

public class Response {

    private String protocolVersion = "HTTP/1.1";
    private String status;
    private Map<String, String> headers = new HashMap<>();
    private String body;
    private ByteBuffer buffer;

    public Response status(int statusCode) throws IllegalArgumentException {
        if (statusCode < 100 || statusCode > 600) {
            throw new IllegalArgumentException("invalid status code");
        }

        switch (statusCode) {
            case 200 -> this.status = "200 OK";
            case 404 -> this.status = "404 NOT FOUND";
            case 405 -> this.status = "405 METHOD NOT ALLOWED";
        }

        return this;
    }

    public Response body(String body) {
        if (body.isEmpty()) {
            throw new IllegalArgumentException("body is empty");
        }

        this.body = body;
        this.headers.put("Content-Lenght", String.valueOf(body.length()));
        this.headers.put("Content-Type", "*/*");

        return this;
    }

    public Response html(String html) {
        if (html.isEmpty()) {
            throw new IllegalArgumentException("html is empty");
        }

        this.body = html;
        this.headers.put("Content-Type", "text/html; charset=UTF-8");
        this.headers.put("Content-Lenght", String.valueOf(html.length()));
        this.headers.put("Connection", "close");

        return this;
    }

    public ByteBuffer build() {
        if (this.buffer == null) {
            throw new IllegalStateException("buffer not set");
        }

        StringBuilder message = new StringBuilder();

        message.append(String.format("%s %s\r\n", this.protocolVersion, this.status));
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            message.append(String.format("%s: %s\r\n", entry.getKey(), entry.getValue()));
        }

        message.append("\r\n\r\n");

        if (!this.body.isEmpty()) {
            message.append(this.body);
        }

        this.buffer.put(message.toString().getBytes());
        return buffer;
    }

    public void setBuffer(ByteBuffer buffer) {
        this.buffer = buffer;
    }
}
