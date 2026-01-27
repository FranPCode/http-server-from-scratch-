import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.ByteBuffer;

import org.junit.jupiter.api.Test;

import httpserver.HTTPStatusCode;
import httpserver.Response;

public class ResponseTest {
    @Test
    void happySimpleResponse() {
        Response response = new Response().status(HTTPStatusCode.OK);

        response.setBuffer(ByteBuffer.allocate(4096));
        ByteBuffer buffer = response.build();

        byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes);

        String message = new String(bytes);
        assertEquals("HTTP/1.1 200 OK\r\n\r\n", message);
    }

    @Test
    void happyResponseWithView() {
        Response response = new Response()
                .status(HTTPStatusCode.OK)
                .view("response-test.html");

        response.setBuffer(ByteBuffer.allocate(4096));
        ByteBuffer buffer = response.build();

        byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes);

        String message = new String(bytes);
        String mustBe = """
                HTTP/1.1 200 OK\r
                Connection: close\r
                Content-Length: 184\r
                Content-Type: text/html; charset=UTF-8\r
                \r
                <!DOCTYPE html>
                <html>
                <head>
                    <title>Test Response</title>
                </head>
                <body>
                    <h1>Test HTML Response</h1>
                    <p>This is a simple test file for Response tests.</p>
                </body>
                </html>
                """;

        assertEquals(mustBe, message);
    }

    @Test
    void happyresponseWithExtraHeaders() {
        String message = """
                HTTP/1.1 200 OK\r
                Test: hello test\r
                \r
                """;

        Response response = new Response().status(HTTPStatusCode.OK);
        response.setHeader("Test", "hello test");

        response.setBuffer(ByteBuffer.allocate(4096));
        ByteBuffer buffer = response.build();

        byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes);

        String responseMessage = new String(bytes);
        assertEquals(message, responseMessage);
    }

    @Test
    void emptyViewPath() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Response().status(HTTPStatusCode.OK).view("");
        });
    }

    // invalid relative path
    @Test
    void invalidRelativePath() {
        assertThrows(RuntimeException.class, () -> {
            new Response().status(HTTPStatusCode.OK).view("invalid-path.html");
        });
    }

    // when a buffer is not setted
    @Test
    void bufferNotSetted() {
        assertThrows(IllegalStateException.class, () -> {
            Response response = new Response().status(HTTPStatusCode.OK);
            response.build();
        });
    }

    @Test
    void statusNotSet() {
        assertThrows(IllegalStateException.class, () -> {
            Response response = new Response();
            response.setBuffer(ByteBuffer.allocate(4096));
            response.build();
        });
    }

    @Test
    void setHeaderWithNumber() {
        Response response = new Response().status(HTTPStatusCode.OK);
        response.setHeader("Content-Length", 1234);

        response.setBuffer(ByteBuffer.allocate(4096));
        ByteBuffer buffer = response.build();

        byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes);
        String message = new String(bytes);

        assertEquals("HTTP/1.1 200 OK\r\nContent-Length: 1234\r\n\r\n", message);
    }

    @Test
    void overwriteExistingHeader() {
        Response response = new Response().status(HTTPStatusCode.OK);
        response.setHeader("Test", "first value");
        response.setHeader("Test", "second value");

        response.setBuffer(ByteBuffer.allocate(4096));
        ByteBuffer buffer = response.build();

        byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes);
        String message = new String(bytes);

        assertEquals("HTTP/1.1 200 OK\r\nTest: second value\r\n\r\n", message);
    }
}
