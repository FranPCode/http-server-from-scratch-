import httpserver.HTTPStatusCode;
import httpserver.Request;
import httpserver.Response;
import httpserver.controllers.Controller;

public class TestController extends Controller {

    @Override
    public Response execute(Request request) {

        return new Response().status(HTTPStatusCode.OK);
    }
}
