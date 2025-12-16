package httpserver.router;

import java.util.ArrayList;
import java.util.List;

import httpserver.Request;
import httpserver.Response;
import httpserver.controllers.Controller;

public class Router {
    private List<Route> container = new ArrayList<>();

    public void add(String method, String resource, Controller controller) {
        container.add(new Route(method, resource, controller));
    }

    public Response resolve(Request request) throws IllegalArgumentException {
        Route matchedRoute = null;
        for (Route route : container) {
            if (route.getResource().equals(request.getResource())) {
                matchedRoute = route;
                break;
            }
        }

        if (matchedRoute.equals(null)) {
            throw new IllegalArgumentException("resource does not exists");
        }

        Controller controller = matchedRoute.getController();
        return controller.execute(request);
    }

    public boolean exists(String resource) {

        if (resource.isEmpty()) {
            throw new IllegalArgumentException("resource is empty");
        }

        for (Route route : container) {
            if (route.getResource().equals(resource)) {
                return true;
            }
        }

        return false;
    }

}
