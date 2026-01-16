import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import httpserver.Request;

public class RequestTest {

    @Test
    void happyRequest() {
        String text = "DELETE /test HTTP/1.1\r\nHost: 0.0.0.0\r\nContent-Type: text/html\r\nContent-Length: 19\r\n\r\n<h1>Hello test</h1>";
        Request request = new Request(text);

        assertEquals(request.getMethod(), "DELETE");
        assertEquals(request.getResource(), "/test");
        assertEquals(request.getProtocolVersion(), "HTTP/1.1");

        assertEquals(request.getHeaders().get("content-type"), "text/html");
        assertEquals(request.getHeaders().get("content-length"), "19");
        assertEquals(request.getHeaders().get("host"), "0.0.0.0");
    }

    @Test
    void happyRequestWithNoBody() {
        String text = "DELETE /test HTTP/1.1\r\nHost: 0.0.0.0\r\nTest: testing\r\n\r\n";
        Request request = new Request(text);

        assertEquals(request.getMethod(), "DELETE");
        assertEquals(request.getResource(), "/test");
        assertEquals(request.getProtocolVersion(), "HTTP/1.1");

        assertEquals(request.getHeaders().get("test"), "testing");
        assertEquals(request.getHeaders().get("content-length"), null);
        assertEquals(request.getHeaders().get("host"), "0.0.0.0");
    }

    @Test
    void happyRequestWithNoHeaders() {
        String text = "DELETE /test HTTP/1.1\r\n\r\n";
        Request request = new Request(text);

        assertEquals(request.getMethod(), "DELETE");
        assertEquals(request.getResource(), "/test");
        assertEquals(request.getProtocolVersion(), "HTTP/1.1");
        assertEquals(request.getBody(), "");
        assertEquals(request.getHeaders().size(), 0);
    }

    @ParameterizedTest(name = "{0} should throw an error")
    @ValueSource(strings = {
            "",
            "GET /test HTTP/1.1\r\nHost: 127.0.0.1\r\n",
            "GET /test HTTP/1.1\r\nContent-Length: 19\r\n\r\n<h1>Hello test</h1>",
            "GET /test HTTP/1.1\r\nContent-Type: text/html\r\nContent-Length: 20\r\n\r\n<h1>Hello test</h1>",
            "GET /test HTTP/1.1\r\nContent-Type: text/html\r\n\r\n",
    })
    void invalidRequest(String requestText) {
        assertThrows(IllegalArgumentException.class, () -> {
            new Request(requestText);
        });
    }

}
