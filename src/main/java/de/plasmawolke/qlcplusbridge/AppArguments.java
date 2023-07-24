package de.plasmawolke.qlcplusbridge;

import com.beust.jcommander.Parameter;

import java.net.InetAddress;

public class AppArguments {

    @Parameter(names = {"-h", "--host"}, description = "The inet address of the bridge, e.g. 192.168.23.138")
    private String address = null;

    @Parameter(names = {"-p", "--port"}, description = "The port of the bridge, e.g. 9123")
    private int port = 9123;

    @Parameter(names = {"-vch", "--virtual-console-host"}, description = "The host of the QLC+ Virtual Web Console")
    private String qlcPlusWebHost = "localhost";

    @Parameter(names = {"-vcp", "--virtual-console-port"}, description = "The port of the QLC+ Virtual Web Console")
    private int qlcPlusWebPort = 9999;

    public String getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }

    public String getQlcPlusWebHost() {
        return qlcPlusWebHost;
    }

    public int getQlcPlusWebPort() {
        return qlcPlusWebPort;
    }

    @Override
    public String toString() {
        return "AppArguments{" +
                "port=" + port +
                ", qlcPlusWebHost='" + qlcPlusWebHost + '\'' +
                ", qlcPlusWebPort=" + qlcPlusWebPort +
                '}';
    }


}
