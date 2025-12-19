package httpserver.controllers;

import httpserver.Request;
import httpserver.Response;

public class HomeController extends Controller {

    @Override
    public Response execute(Request request) {
        return new Response().status(200).view("index.html");
    }
}
