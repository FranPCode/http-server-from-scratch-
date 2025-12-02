package httpserver.parsers;

import java.util.HashMap;
import java.util.Map;

public class RequestParser {

    public static Map<String, String> requestLine(String line) throws IllegalArgumentException {
        String[] requestLine = line.split(" ", 3);

        int numberParts = 3;
        if (requestLine.length != numberParts) {
            throw new IllegalArgumentException("request line is invalid");
        }

        String method = requestLine[0].trim();
        String resource = requestLine[1].trim();
        String protocolVersion = requestLine[2].trim();

        if (!method.matches("[A-Z]+")
                || !resource.startsWith("/")
                || !protocolVersion.matches("HTTP/[0-9]\\.[0-9]")) {
            throw new IllegalArgumentException("invalid requestline syntaxis");
        }

        Map<String, String> items = new HashMap<>();
        items.put("method", method);
        items.put("resource", resource);
        items.put("protocol-version", protocolVersion);

        return items;
    }

    public static HashMap<String, String> headers(String[] fieldLines) throws IllegalArgumentException {
        HashMap<String, String> headers = new HashMap<>();

        for (int i = 1; i < fieldLines.length; i++) {
            if (!fieldLines[i].contains(": ")) {
                throw new IllegalArgumentException("headers invalid field line syntax");
            }

            String[] fieldLine = fieldLines[i].split(": ", 2);
            String headerName = fieldLine[0];
            String headerValue = fieldLine[1];

            if (headerValue.isEmpty()
                    || !headerName.matches("^[A-Z][a-z]*(?:-[A-Z][a-z]*)*$")) {

                throw new IllegalArgumentException("header value is empty");
            }

            headers.put(
                    headerName.toLowerCase().trim(),
                    headerValue.trim());
        }

        return headers;

    }

}
