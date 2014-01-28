package se.edstrompartners.net.command;

import se.edstrompartners.net.events.Handshake;

public enum CommandType {

    HANDSHAKE(Handshake.class);

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
