package httpserver;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

public class Response {

    private String protocolVersion = "HTTP/1.1";
    private String status;
    private Map<String, String> headers = new HashMap<>();
    private String body;

    public Response status(int statusCode) {
        switch (statusCode) {
            case 200 -> this.status = "200 OK";
            case 405 -> this.status = "405 METHOD NOT ALLOWED";
        }

        return this;
    }

    public Response body(String body) {
        this.body = body;
        this.headers.put("Content-Lenght", String.valueOf(body.length()));

        return this;
    }

    public ByteBuffer send() {
        ByteBuffer buffer = ByteBuffer.allocateDirect(2048);
        String statusLine = this.protocolVersion + " " + this.status + "\r\n";

        buffer.put(statusLine.getBytes());
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            String fieldLine = entry.getKey() + ": " + entry.getValue() + "\r\n";
            buffer.put(fieldLine.getBytes());
        }

        buffer.put("\r\n".getBytes());

        if (!this.body.isEmpty()) {
            buffer.put(this.body.getBytes());
        }

        return buffer;
    }
}
