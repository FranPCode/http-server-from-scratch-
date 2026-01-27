package httpserver;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;

public class Response {

    private String protocolVersion = "HTTP/1.1";
    private String status;
    private Map<String, String> headers = new HashMap<>();
    private ByteBuffer body;
    private ByteBuffer buffer;

    public Response status(HTTPStatusCode statusCode) {
        this.status = statusCode.getStatusLine();
        return this;
    }

    public Response view(String relativePath) throws IllegalArgumentException, RuntimeException {
        if (relativePath.isEmpty()) {
            throw new IllegalArgumentException("required view path");
        }

        Path path = FileSystems.getDefault().getPath("public", relativePath);

        if (!Files.exists(path)) {
            throw new RuntimeException(String.format("file %s not found", relativePath));
        }

        try {
            FileChannel file = FileChannel.open(path, StandardOpenOption.READ);
            long fileSize = file.size();
            this.body = ByteBuffer.allocate((int) fileSize);
            file.read(this.body);
            this.body.flip();
            file.close();
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to read file: " + path, e);
        }

        this.headers.put("Content-Type", "text/html; charset=UTF-8");
        this.headers.put("Content-Length", String.valueOf(this.body.limit()));
        this.headers.put("Connection", "close");

        return this;
    }

    public ByteBuffer build() {
        if (this.buffer == null) {
            throw new IllegalStateException("buffer not set");
        }

        if (this.status == null) {
            throw new IllegalStateException("status is required");
        }

        StringBuilder message = new StringBuilder();

        message.append(String.format("%s %s\r\n", this.protocolVersion, this.status));
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            message.append(String.format("%s: %s\r\n", entry.getKey(), entry.getValue()));
        }

        message.append("\r\n");
        this.buffer.put(message.toString().getBytes());

        if (this.body != null) {
            this.buffer.put(this.body);
        }

        buffer.flip();
        return buffer;
    }

    public void setBuffer(ByteBuffer buffer) {
        this.buffer = buffer;
    }

    public void setHeader(String headerName, Number value) {
        this.headers.put(headerName, String.valueOf(value));
    }

    public void setHeader(String headerName, String value) {
        this.headers.put(headerName, value);
    }
}
