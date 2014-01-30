package se.edstrompartners.net.events;

import se.edstrompartners.net.command.Command;
import se.edstrompartners.net.command.CommandDecoder;
import se.edstrompartners.net.command.CommandEncoder;
import se.edstrompartners.net.command.CommandType;

public class Message extends Command {

    public String source;
    public String message;

    public Message() {
        // TODO Auto-generated constructor stub
    }

    public Message(String src, String msg) {
        source = src;
        message = msg;
    }

    @Override
    public void decode(CommandDecoder cd) {
        source = cd.decodeString();
        message = cd.decodeString();

    }

    @Override
    public void encode(CommandEncoder ce) {
        ce.encode(source);
        ce.encode(message);

    }

    @Override
    public CommandType getType() {
        return CommandType.MESSAGE;
    }

}
