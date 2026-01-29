import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import httpserver.HTTPStatusCode;
import httpserver.Request;
import httpserver.Response;
import httpserver.controllers.Controller;
import httpserver.router.Router;

public class RouterTest {
    private static Controller testController;
    private static Request request;

    @BeforeAll
    static void setUp() {
        String message = """
                GET /test HTTP/1.1\r
                Test: test\r
                \r
                """;
        request = new Request(message);
        testController = new TestController();
    }

    @Test
    void happyRouter() {
        Router router = new Router();
        router.add("GET", "/test", testController);
        Response response = router.resolve(request);
        Response expectedResponse = new Response().status(HTTPStatusCode.OK);

        assertTrue(router.exists("/test"));
        assertFalse(router.exists("/false"));
        assertEquals(expectedResponse.getStatus(), response.getStatus());
        assertEquals(expectedResponse.getHeaders(), response.getHeaders());
        assertEquals(expectedResponse.getProtocolVersion(), response.getProtocolVersion());
    }

    @Test
    void resolveNonExistingRoute() {
        Router router = new Router();
        router.add("GET", "/test", testController);

        String message = """
                GET /nonexistent HTTP/1.1\r
                Test: test\r
                \r
                """;
        Request nonExistentRequest = new Request(message);
        assertThrows(NullPointerException.class, () -> {
            router.resolve(nonExistentRequest);
        });
    }

    @Test
    void existsWithEmptyString() {
        Router router = new Router();
        router.add("GET", "/test", testController);
        assertThrows(IllegalArgumentException.class, () -> {
            router.exists("");
        });
    }

    @Test
    void resourceDoesNotExist() {
        Router router = new Router();
        router.add("GET", "/test", testController);

        assertFalse(router.exists("/nonexistent"));
        assertFalse(router.exists("/another-route"));
    }
}
