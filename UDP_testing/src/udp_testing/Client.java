/**
 *
 * @author Mike Meding
 *
 */
package udp_testing;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.sql.Time;

public class Client {

    public static void main(String[] args) throws Exception {

        while (true) {
            System.out.println("Server Time >>>>");

            //Create empty socket
            DatagramSocket cs = new DatagramSocket();            
            //get localhost ip 127.0.0.1
            InetAddress ip = InetAddress.getByName("localhost");
            //allocate packet data space
            byte[] rd = new byte[100]; //recieved data
            byte[] sd = new byte[100]; //sending data

            //create empty data packet to be sent
            DatagramPacket sp = new DatagramPacket(sd, sd.length, ip, 1234);
            System.out.println("send data: " + sp.toString());
            //setup recieve packet for data
            DatagramPacket rp = new DatagramPacket(rd, rd.length);

            
            //send empty data
            cs.send(sp);
            //recieve time data
            cs.receive(rp);
            
            String time = new String(rp.getData()); //get epoch time as a string
            try{
                //parse string to long
                //System.out.println(time);
                long epoch = Long.parseLong(time,10);
                //create accurate time object
                Time tm = new Time(epoch);     
                //Print out nicely formatted time.
                System.out.println(tm.getHours() +":"+ tm.getMinutes() +":"+ tm.getSeconds());
            } catch(NumberFormatException nfe) {
                nfe.printStackTrace();
            }
            

            cs.close();
            
            Thread.sleep(5000); //pause for 5 seconds
        }

    }
}
