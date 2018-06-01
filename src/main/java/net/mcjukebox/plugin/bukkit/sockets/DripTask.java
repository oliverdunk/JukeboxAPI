package net.mcjukebox.plugin.bukkit.sockets;

public class DripTask implements Runnable {

    private SocketHandler socketHandler;

    public DripTask(SocketHandler socketHandler) {
        this.socketHandler = socketHandler;
    }

    @Override
    public void run() {
        if (socketHandler.getServer().connected()) {
            socketHandler.getDropListener().setLastDripSent(System.currentTimeMillis());
            socketHandler.emit("drip", null);
        }
    }

}
