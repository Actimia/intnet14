package se.edstrompartners.net.events;

import se.edstrompartners.net.command.Command;
import se.edstrompartners.net.command.CommandDecoder;
import se.edstrompartners.net.command.CommandEncoder;
import se.edstrompartners.net.command.CommandType;

public class ListUsersResponse extends Command {

    public String[] users;

    public ListUsersResponse() {

    }

    public ListUsersResponse(String[] users) {
        this.users = users;
    }

    @Override
    public void decode(CommandDecoder cd) {
        users = cd.decodeStrings();
    }

    @Override
    public void encode(CommandEncoder ce) {
        ce.encode(users);
    }

    @Override
    public CommandType getType() {
        return CommandType.LISTUSERSRESPONSE;
    }

}
