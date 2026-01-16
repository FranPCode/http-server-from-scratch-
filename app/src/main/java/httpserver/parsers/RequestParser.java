package httpserver.parsers;

import java.util.HashMap;
import java.util.Map;

import httpserver.HTTPMethod;

public class RequestParser {

    public static Map<String, String> requestLine(String line) throws IllegalArgumentException {
        if (line.isEmpty()) {
            throw new IllegalArgumentException("request line is invalid");
        }

        String[] requestLine = line.split(" ", 3);

        int numberParts = 3;
        if (requestLine.length != numberParts) {
            throw new IllegalArgumentException("request line is invalid");
        }

        HTTPMethod method = HTTPMethod.valueOf(requestLine[0].trim());
        String resource = requestLine[1].trim();
        String protocolVersion = requestLine[2].trim();

        if (!resource.startsWith("/")
                || !protocolVersion.matches("HTTP/[0-9]\\.[0-9]")) {
            throw new IllegalArgumentException("request line is invalid");
        }

        Map<String, String> items = new HashMap<>();
        items.put("method", method.toString());
        items.put("resource", resource);
        items.put("protocol-version", protocolVersion);

        return items;
    }

    public static HashMap<String, String> headers(String[] fieldLines) throws IllegalArgumentException {
        HashMap<String, String> headers = new HashMap<>();

        if (fieldLines.length == 0) {
            return headers;
        }

        for (int i = 0; i < fieldLines.length; i++) {
            if (!fieldLines[i].contains(": ")) {
                throw new IllegalArgumentException("headers invalid field line syntax");
            }

            String[] fieldLine = fieldLines[i].split(": ", 2);
            String headerName = fieldLine[0];
            String headerValue = fieldLine[1];

            if (headerValue.isEmpty()
                    || !headerName.matches("^[A-Za-z0-9!#$%&'*+\\-.^_`|~]+$")) {

                throw new IllegalArgumentException("invalid header name or value");
            }

            headers.put(
                    headerName.toLowerCase().trim(),
                    headerValue.trim());
        }

        return headers;

    }

}
