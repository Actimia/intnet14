package se.edstrompartners.net.events;

import se.edstrompartners.net.command.Command;
import se.edstrompartners.net.command.CommandDecoder;
import se.edstrompartners.net.command.CommandEncoder;
import se.edstrompartners.net.command.CommandType;


public class ListUsers extends Command {

    @Override
    public void decode(CommandDecoder cd) {

    }

    @Override
    public void encode(CommandEncoder ce) {

    }

    @Override
    public CommandType getType() {
        return CommandType.LISTUSERS;
    }

}
