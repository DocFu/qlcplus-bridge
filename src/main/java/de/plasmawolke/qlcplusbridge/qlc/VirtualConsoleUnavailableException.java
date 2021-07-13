package de.plasmawolke.qlcplusbridge.qlc;

public class VirtualConsoleUnavailableException extends Exception{

    VirtualConsoleUnavailableException(String url){
        super(url);
    }

    VirtualConsoleUnavailableException(String url, Throwable t){
        super(url, t);
    }

    public String getUrl(){
        return this.getMessage();
    }

}
