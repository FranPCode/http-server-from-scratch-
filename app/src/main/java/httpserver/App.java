package httpserver;

public class App {
    private static int port = 8080;

    public static void main(String[] args) throws Exception {
        Server server = new Server(new SelectorHandler(), port);

        System.out.println("server listening in port " + port);
        server.start();
    }
}
