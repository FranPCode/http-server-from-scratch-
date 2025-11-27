import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class SelectorHandler {
    private Selector selector;

    public SelectorHandler() throws IOException {
        this.selector = Selector.open();
    }

    public void start() throws IOException {
        while (true) {
            this.selector.select(10000);

            Iterator<SelectionKey> keys = selector.selectedKeys().iterator();
            while (keys.hasNext()) {
                SelectionKey key = keys.next();
                keys.remove();

                if (key.isAcceptable()) {
                    accept(key);
                }

                if (key.isReadable()) {
                    read(key);
                }

                if (key.isWritable()) {
                    write(key);
                }

            }
        }
    }

    private void accept(SelectionKey key) throws IOException {
        ServerSocketChannel server = (ServerSocketChannel) key.channel();
        SocketChannel client = server.accept();
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        StringBuilder acumulated = new StringBuilder();

        client.configureBlocking(false);
        SelectionKey clientKey = client.register(this.selector, SelectionKey.OP_READ);
        clientKey.attach(new ClientState(buffer, acumulated));
    }

    public void read(SelectionKey key) throws IOException {
        SocketChannel client = (SocketChannel) key.channel();
        ClientState state = (ClientState) key.attachment();
        ByteBuffer buffer = state.getBuffer();

        int bytesRead = client.read(buffer);

        if (bytesRead == -1) {
            key.cancel();
            client.close();
            return;
        }

        if (bytesRead == 0) {
            return;
        }

        buffer.flip();

        StringBuilder message = state.getAcumulated();

        while (buffer.hasRemaining()) {
            message.append((char) buffer.get());
        }

        buffer.compact();

        System.out.println(message);

        if (message.toString().contains("\r\n\r\n")) {
            Request request = new Request(message.toString());
            if (request.isCompleted()) {
                state.setRequest(request);
                buffer.clear();
                key.interestOps(SelectionKey.OP_WRITE);
            }

        }

    }

    public void write(SelectionKey key) throws IOException {
        SocketChannel client = (SocketChannel) key.channel();
        ClientState state = (ClientState) key.attachment();
        ByteBuffer buffer = state.getBuffer();
        Request request = state.getRequest();

        if (request.getMethod().equals("GET")) {
            byte[] bodyBytes = "Hello, putita!\r\n".getBytes();
            String host = "Host: " + request.getHeaders().get("host");
            byte[] hostBytes = host.getBytes();
            buffer.put("HTTP/1.1 200 OK\r\n".getBytes());
            buffer.put(hostBytes);
            buffer.put("Content-Type: text/plain\r\n".getBytes());
            buffer.put(("Content-Length: " + bodyBytes.length + "\r\n").getBytes());
            buffer.put("\r\n".getBytes());
            buffer.put(bodyBytes);

        } else {
            byte[] bodyBytes = "no puedes pasar!\r\n".getBytes();
            buffer.put("HTTP/1.1 405 METHOD NOT ALLOWED\r\n".getBytes());
            buffer.put("Content-Type: text/plain\r\n".getBytes());
            buffer.put(("Content-Length: " + bodyBytes.length + "\r\n").getBytes());
            buffer.put("\r\n".getBytes());
            buffer.put(bodyBytes);
        }

        buffer.flip();

        client.write(buffer);

        buffer.clear();
        key.cancel();
        client.close();
    }

    public Selector getSelector() {
        return this.selector;
    }
}

class ClientState {
    private ByteBuffer buffer;
    private StringBuilder acumulated;
    private Request request;

    public ClientState(ByteBuffer buffer, StringBuilder acumulated) {
        this.buffer = buffer;
        this.acumulated = acumulated;
    }

    public ByteBuffer getBuffer() {
        return this.buffer;
    }

    public StringBuilder getAcumulated() {
        return this.acumulated;
    }

    public Request getRequest() {
        return this.request;
    }

    public void setRequest(Request request) {
        this.request = request;
    }

}