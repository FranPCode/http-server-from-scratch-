package httpserver.controllers;

import httpserver.Request;
import httpserver.Response;

public abstract class Controller {

    public abstract Response execute(Request request);
}
