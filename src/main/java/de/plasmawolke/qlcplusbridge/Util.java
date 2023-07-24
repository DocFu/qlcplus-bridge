package de.plasmawolke.qlcplusbridge;

import java.net.*;

public class Util {

    public static InetAddress getInetAddress(String address){

        if(address == null){
            try (final DatagramSocket datagramSocket = new DatagramSocket()) {
                datagramSocket.connect(InetAddress.getByName("8.8.8.8"), 12345);
                return InetAddress.getByName(datagramSocket.getLocalAddress().getHostAddress());
            } catch (UnknownHostException | SocketException e) {
                e.printStackTrace();
            }
        }

        if(address != null){
            try{
                return InetAddress.getByName(address);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }


        try{
            return InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        return null;

    }
}
