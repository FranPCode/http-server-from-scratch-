import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;

public class Server {
    private final int port;
    private SelectorHandler selector;

    public Server(SelectorHandler selector, int port) {
        this.selector = selector;
        this.port = port;
    }

    public void start() throws IOException {
        ServerSocketChannel server = ServerSocketChannel.open();
        server.configureBlocking(false);
        server.bind(new InetSocketAddress(this.port));

        server.register(selector.getSelector(), SelectionKey.OP_ACCEPT);
        selector.start();

    }

    public int getPort() {
        return this.port;
    }
}
