package JNP.jnfc;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

/**
 * Created by mitya on 10/14/17.
 */
public class IPFIXCol {
    public final static int PORT = 6555;
    public final static int MAX_PACKET_SIZE = 65507;
    public static TemplateCache templateCache;
    public static void main(String [] args) {
        try {
            templateCache = new TemplateCache();
            DatagramChannel channel = DatagramChannel.open();
            DatagramSocket socket = channel.socket();
            SocketAddress address = new InetSocketAddress(PORT);
            socket.bind(address);
            ByteBuffer buffer = ByteBuffer.allocateDirect(MAX_PACKET_SIZE);
            while (true) {
                SocketAddress client = channel.receive(buffer);
                buffer.flip();
                //channel.send(buffer, client);
                System.out.println(client);
                //System.out.println(buffer.toString());
                IPFIXDatagram datagram = new IPFIXDatagram(buffer);
                datagram.parse();
                datagram = null;
                buffer.clear();
            }
        } catch (IOException ex) {
            System.err.println(ex);
        }
    }
}
