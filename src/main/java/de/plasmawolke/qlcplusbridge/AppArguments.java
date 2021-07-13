package de.plasmawolke.qlcplusbridge;

import com.beust.jcommander.Parameter;

public class AppArguments {


    @Parameter(names = {"-p", "--port"}, description = "The port of the bridge, e.g. 9123")
    private int port = 9123;

    @Parameter(names = {"-vch", "--virtual-console-host"}, description = "The host of the QLC+ Virtual Web Console")
    private String qlcPlusWebHost = "localhost";

    @Parameter(names = {"-vcp", "--virtual-console-port"}, description = "The port of the QLC+ Virtual Web Console")
    private int qlcPlusWebPort = 9999;


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
