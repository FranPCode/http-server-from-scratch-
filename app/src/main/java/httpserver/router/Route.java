package httpserver.router;

import httpserver.controllers.Controller;

public class Route {
    private String method;
    private String resource;
    private Controller controller;

    public Route(String method, String resource, Controller controller) {
        this.method = method;
        this.resource = resource;
        this.controller = controller;
    }

    public String getMethod() {
        return method;
    }

    public String getResource() {
        return resource;
    }

    public Controller getController() {
        return controller;
    }

}
