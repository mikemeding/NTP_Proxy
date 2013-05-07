/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package udp_testing;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import os.util.ByteHelper;

/**
 *
 * @author Mike Meding
 */
public class ntpProxy {

    public static void main(String[] args) throws Exception {
        DatagramSocket ss = new DatagramSocket(123);

        while (true) {

            System.out.println("Server is up....");

            byte[] rd = new byte[100];

            //setup recieve bytes from localhost NTP
            DatagramPacket rp = new DatagramPacket(rd, rd.length);
            // will wait here for packet before sending one back.
            ss.receive(rp); // ss specifies the port and inet info


            //Printing out effective address from search
//            System.out.println(rp.getAddress().toString() + " " + rp.getPort());
//            String message = ByteHelper.dump(rp.getData());
//            System.out.println(message);

            //SEND the bytes recived from localhost to Controller
            //getting new ip address
            InetAddress sip = InetAddress.getByName("10.0.0.53");
            //creating packet from recieved data to send onward
            DatagramPacket sp = new DatagramPacket(rp.getData(), rp.getData().length, sip, 123);

            //creating recieve packet from controller
            byte[] rd2 = new byte[256];
            DatagramPacket rcp = new DatagramPacket(rd2, rd2.length);

            //send packet from localhost to controller
            ss.send(sp);
            //recieve packet from controller to send to localhost
            ss.receive(rcp);

            //print data recieved from controller.
            System.out.print(rcp.getAddress() + " " + rcp.getPort());
            System.out.println(ByteHelper.dump(rp.getData()));
            
            //creating final send packet back to server with data from controller.
            InetAddress ApolloIp = rcp.getAddress(); //tried with both "localhost" and now with getting the send address. no luck...      
            DatagramPacket ApolloSend = new DatagramPacket(rcp.getData(),rcp.getData().length,rp.getAddress(),rp.getPort());
            
            ss.send(ApolloSend);


        }
    }
}
