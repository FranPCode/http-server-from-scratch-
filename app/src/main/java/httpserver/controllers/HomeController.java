package httpserver.controllers;

import httpserver.Request;
import httpserver.Response;

public class HomeController extends Controller {

    @Override
    public Response execute(Request request) {
        String html = "<h1>hello world</h1>";
        return new Response().status(200).html(html);
    }
}
