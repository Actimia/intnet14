package se.edstrompartners.net.command;

import se.edstrompartners.net.events.Handshake;
import se.edstrompartners.net.events.Message;
import se.edstrompartners.net.events.NetworkShutdown;
import se.edstrompartners.net.events.PrivateMessage;

public enum CommandType {

    HANDSHAKE(Handshake.class), MESSAGE(Message.class), NETWORKSHUTDOWN(NetworkShutdown.class), LISTUSERS(
            ListUsers.class), PRIVATEMESSAGE(PrivateMessage.class);

    private Class<? extends Command> cls;

    private CommandType(Class<? extends Command> cls) {
        this.cls = cls;
    }

    public Class<? extends Command> getCls() {
        return cls;
    }

    public int getID() {
        return ordinal();
    }
}
