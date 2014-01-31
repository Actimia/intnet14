package se.edstrompartners.net.command;


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
