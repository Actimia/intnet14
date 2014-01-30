package se.edstrompartners.net.command;

import java.io.IOException;

import se.edstrompartners.intnet14.lab1.Network;

public interface CommandListener {

    public void onCommand(Network src, Command com) throws IOException;

}
