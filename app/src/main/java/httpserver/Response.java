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

    public Response(ByteBuffer buffer) {
        this.buffer = buffer;
    }

    public Response status(int statusCode) throws IllegalArgumentException {
        if (statusCode < 100 || statusCode > 600) {
            throw new IllegalArgumentException("invalid status code");
        }

        switch (statusCode) {
            case 200 -> this.status = "200 OK";
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

    public ByteBuffer send() {
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
}
