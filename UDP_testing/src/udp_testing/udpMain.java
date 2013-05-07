/**
 *
 * @author Mike Meding
 *
 */
package udp_testing;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Date;

public class udpMain {

    public static void main(String[] args) throws Exception {

        DatagramSocket ss = new DatagramSocket(123);

        while (true) {

            System.out.println("Server is up....");

            byte[] rd = new byte[100];
            byte[] sd = new byte[100];

            //recieve 100 bytes
            DatagramPacket rp = new DatagramPacket(rd, rd.length);
            // will wait here for packet before sending one back.
            ss.receive(rp); // ss specifies the port and inet info


            InetAddress ip = rp.getAddress(); //gets the current inet address of the system
            int port = rp.getPort();

            //Printing out effective address from search
            System.out.println(rp.getAddress().toString() + " " + rp.getPort());

            
            Date d = new Date();   // creates system time object
            String time = d.getTime() + "";  // epoch time converted to String
            sd = time.getBytes();  // converting that String to byte for sending
            
            //Printing out time conversion
            System.out.println(time + " --> " + time.getBytes().toString());
            
            //Create DatagramPacket out of time data to be sent
            DatagramPacket sp = new DatagramPacket(sd, sd.length, ip, port);
            //Send packet
            ss.send(sp);

            //reset packet
            rp = null;
            System.out.println("Packet Sent.");

        }

    }
}
