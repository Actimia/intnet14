package se.edstrompartners.net.events;

import se.edstrompartners.net.command.Command;
import se.edstrompartners.net.command.CommandDecoder;
import se.edstrompartners.net.command.CommandEncoder;
import se.edstrompartners.net.command.CommandType;

public class Handshake extends Command {
    public String name;

    public Handshake(String username) {
        this.name = username;
    }

    public Handshake() {

    }

    @Override
    public void decode(CommandDecoder cd) {
        name = cd.decodeString();
    }

    @Override
    public void encode(CommandEncoder ce) {
        ce.encode(name);
    }

    @Override
    public CommandType getType() {
        return CommandType.HANDSHAKE;
    }

}