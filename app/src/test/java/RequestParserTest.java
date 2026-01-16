import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import httpserver.parsers.RequestParser;

public class RequestParserTest {

    @ParameterizedTest(name = "requestLine {0} should throw exception")
    @ValueSource(strings = {
            "",
            "GET /test HTTP/1.1 test",
            "GET test HTTP/1.1",
            "GET test HTTP/a.1",
            "TEST /test HTTP/1.1",
    })
    void testInvalidArguments(String requestLine) {
        assertThrows(IllegalArgumentException.class, () -> {
            RequestParser.requestLine(requestLine);
        });
    }

    @Test
    void testRequestLine() {
        Map<String, String> line = RequestParser.requestLine(
                "POST /test HTTP/1.1");

        assertEquals(line.get("method"), "POST");
        assertEquals(line.get("resource"), "/test");
        assertEquals(line.get("protocol-version"), "HTTP/1.1");
    }

    static Stream<Arguments> correctHeaderFieldlineSyntax() {
        return Stream.of(
                Arguments.of((Object) new String[] { "" }),
                Arguments.of((Object) new String[] { "Content-Type text/html" }),
                Arguments.of((Object) new String[] { "Content-Type: " }),
                Arguments.of((Object) new String[] { "Content-Type : text/html" }),
                Arguments.of((Object) new String[] { "Content Type: text/html" }),
                Arguments.of((Object) new String[] { "(Content: text/html" }));
    }

    @ParameterizedTest(name = "Headers {0} should throw an error")
    @MethodSource()
    void correctHeaderFieldlineSyntax(String[] headers) {

        assertThrows(IllegalArgumentException.class, () -> {
            RequestParser.headers(headers);
        });
    }

    @Test
    void correctHeaderOutput() {
        String[] headers = {
                "Content-Type: application/json",
                "Content-Length: 1024",
                "Host: 127.0.0.1"
        };

        HashMap<String, String> map = RequestParser.headers(headers);

        assertEquals(map.get("content-type"), "application/json");
        assertEquals(map.get("content-length"), "1024");
        assertEquals(map.get("host"), "127.0.0.1");
    }

    @Test
    void emptyHeaders() {
        String[] empty = {};
        HashMap<String, String> headers = RequestParser.headers(empty);

        assertEquals(headers.size(), 0);
    }
}
