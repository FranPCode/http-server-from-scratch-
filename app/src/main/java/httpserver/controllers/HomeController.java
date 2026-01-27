package httpserver.controllers;

import httpserver.HTTPStatusCode;
import httpserver.Request;
import httpserver.Response;

public class HomeController extends Controller {

    @Override
    public Response execute(Request request) {
        return new Response().status(HTTPStatusCode.OK).view("index.html");
    }
}
