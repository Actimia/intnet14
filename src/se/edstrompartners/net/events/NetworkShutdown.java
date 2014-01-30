package se.edstrompartners.net.events;

import se.edstrompartners.net.command.Command;
import se.edstrompartners.net.command.CommandDecoder;
import se.edstrompartners.net.command.CommandEncoder;
import se.edstrompartners.net.command.CommandType;

public class NetworkShutdown extends Command {

    public String token;

    public NetworkShutdown() {
    }

    public NetworkShutdown(String token) {
        this.token = token;
    }

    @Override
    public void decode(CommandDecoder cd) {

    }

    @Override
    public void encode(CommandEncoder ce) {

    }

    @Override
    public CommandType getType() {
        return CommandType.NETWORKSHUTDOWN;
    }

}
