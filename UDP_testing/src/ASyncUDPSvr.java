
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Mike Meding
 */
public class ASyncUDPSvr {

    static int BUF_SZ = 1024;

    class Con {

        ByteBuffer req;
        ByteBuffer resp;
        SocketAddress sa;

        public Con() {
            req = ByteBuffer.allocate(BUF_SZ);
        }
    }
    static int port = 123;
    static String hostname = "127.0.0.1";
    static String ntphost = "10.0.0.88";

    private void process() {
        try {
            Selector selector = Selector.open();
            DatagramChannel channel = DatagramChannel.open();
            InetSocketAddress isa = new InetSocketAddress(hostname, port);
            channel.socket().bind(isa);
            channel.configureBlocking(false);
            SelectionKey clientKey = channel.register(selector, SelectionKey.OP_READ);
            clientKey.attach(new Con());
            while (true) {
                try {
                    selector.select();

                    Iterator selectedKeys = selector.selectedKeys().iterator();
                    while (selectedKeys.hasNext()) {
                        try {
                            SelectionKey key = (SelectionKey) selectedKeys.next();
                            selectedKeys.remove();

                            if (!key.isValid()) {
                                continue;

                            }

                            if (key.isReadable()) {
                                read(key);
                                key.interestOps(SelectionKey.OP_WRITE);
                            } else if (key.isWritable()) {
                                write(key);
                                key.interestOps(SelectionKey.OP_READ);
                            }
                        } catch (IOException e) {
                            System.err.println("glitch, continuing... " + (e.getMessage() != null ? e.getMessage() : ""));
                        }
                    }
                } catch (IOException e) {
                    System.err.println("glitch, continuing... " + (e.getMessage() != null ? e.getMessage() : ""));
                }
            }
        } catch (IOException e) {
            System.err.println("network error: " + (e.getMessage() != null ? e.getMessage() : ""));
        }
    }

    private void read(SelectionKey key) throws IOException {
        DatagramChannel chan = (DatagramChannel) key.channel();
        Con con = (Con) key.attachment();
        con.sa = chan.receive(con.req);

        //socket and address logic
        DatagramSocket cs = new DatagramSocket();
        InetAddress ip = InetAddress.getByName("Apollo");
        byte[] rd = new byte[BUF_SZ];

        // con.req.array() == the byte array that needs to be forwarded.
        // con.resp.array() == the byte array that needs to be sent back when this completes.
        // DatagramPacket sets up the send and recive packages with there storage loactions on the system and where to send them.
        DatagramPacket sp = new DatagramPacket(con.req.array(), 90, ip, 123);
        DatagramPacket rp = new DatagramPacket(rd, rd.length); //where recived data will be put

        // Actually sending the packet.
        cs.send(sp);
        System.out.println("data forwarded...");

        // When send is complete it waits for a response here.
        cs.receive(rp);
        System.out.println("data returned.");
        con.resp = ByteBuffer.wrap(rd);

        //con.resp = Charset.forName("UTF-8").newEncoder().encode(CharBuffer.wrap("send the same string"));

        //printing out what we got back.
        System.out.println("return size=" + rd.length);
    }

    private void write(SelectionKey key) throws IOException {
        DatagramChannel chan = (DatagramChannel) key.channel();
        Con con = (Con) key.attachment();
        chan.send(con.resp, con.sa);
    }

    static public void main(String[] args) {
        ASyncUDPSvr svr = new ASyncUDPSvr();
        svr.process();
    }
}
