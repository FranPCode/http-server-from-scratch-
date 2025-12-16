package httpserver;

import httpserver.controllers.HomeController;
import httpserver.router.Router;

public class App {
    private static int port = 8080;

    public static void main(String[] args) throws Exception {
        Router router = new Router();
        router.add("GET", "/", new HomeController());

        Server server = new Server(new SelectorHandler(router), port);
        System.out.println("server listening in port " + port);
        server.start();
    }
}
