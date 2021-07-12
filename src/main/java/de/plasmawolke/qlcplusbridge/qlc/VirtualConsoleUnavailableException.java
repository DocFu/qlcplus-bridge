package de.plasmawolke.qlcplusbridge.qlc;

public class VirtualConsoleUnavailableException extends Exception{

    VirtualConsoleUnavailableException(String message){
        super(message);
    }

    VirtualConsoleUnavailableException(String message, Throwable t){
        super(message, t);
    }

}
