package game.server.utils;

import java.net.Socket;

public final class Singleton {

    private static Singleton server;

    public Socket socket;

    private Singleton(Socket socket) {
        this.socket = socket;
    }

    public static Singleton getInstance(Socket socket){
        if (server == null) {
            server = new Singleton(socket);
        }
        return server;
    }
}
