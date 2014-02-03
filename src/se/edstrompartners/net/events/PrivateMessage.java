package se.edstrompartners.net.events;

import se.edstrompartners.net.command.Command;
import se.edstrompartners.net.command.CommandDecoder;
import se.edstrompartners.net.command.CommandEncoder;
import se.edstrompartners.net.command.CommandType;

public class PrivateMessage extends Command {

    public String src;
    public String tar;
    public String msg;

    public PrivateMessage(String sender, String recipient, String message) {
        src = sender;
        tar = recipient;
        msg = message;
    }

    public PrivateMessage() {
    }

    @Override
    public void decode(CommandDecoder cd) {
        src = cd.decodeString();
        tar = cd.decodeString();
        msg = cd.decodeString();
    }

    @Override
    public void encode(CommandEncoder ce) {
        ce.encode(src);
        ce.encode(tar);
        ce.encode(msg);
    }

    @Override
    public CommandType getType() {
        return CommandType.PRIVATEMESSAGE;
    }

}
