package se.edstrompartners.net.command;

import java.util.HashMap;

public abstract class Command {

    private static CommandType[] commands = CommandType.values();

    private static HashMap<Class<?>, TypeHandler<?>> typehandlers = new HashMap<>();

    /**
     * Register a typehandler for a given class, to be able to encode them
     * later. Refer to the TypeHandler documentation for further information.
     * Call this before using the
     * <code>CommandEncoder.encode(Class<T>, T)</code> method and the similar
     * one in <code>CommandDecoder</code>.
     * 
     * Calling this again with a class that is already registered will overwrite
     * the handler. If the second argument is <code>null</code>, the class is
     * unregistered.
     * 
     * @param cls
     *            The class to handle.
     * @param handler
     *            The handler for the class.
     */
    public static final <T> void registerTypeHandler(Class<T> cls, TypeHandler<T> handler) {
        typehandlers.put(cls, handler);
    }

    /**
     * Used to fetch a given TypeHandler.
     * 
     * @throws EncodingException
     *             If no handler for the class can be found.
     * @param cls
     *            The class to get the handler for.
     * @return The handler for the class.
     */
    @SuppressWarnings("unchecked")
    public static final <T> TypeHandler<T> getTypeHandler(Class<T> cls) {
        TypeHandler<T> res = (TypeHandler<T>) typehandlers.get(cls);
        if (res == null) {
            throw new EncodingException("No handler registered for class: " + cls.toString());
        }
        return res;
    }

    /**
     * Decodes a given byte[] into a command. This method will find which
     * command is encoded and decode it, or throw an exception.
     * 
     * @param bytes
     * @return
     */
    public static Command decode(byte[] bytes) {
        try {
            CommandDecoder cd = new CommandDecoder(bytes);
            Class<? extends Command> cls = commands[cd.decodedID()].getCls();
            Command res = cls.newInstance();
            res.decode(cd);
            return res;
        } catch (InstantiationException | IllegalAccessException e) {
            throw new EncodingException("Could not find a no-args constructor.");
        }
    }

    public static byte[] encode(Command com) {
        CommandEncoder ce = new CommandEncoder(com);
        com.encode(ce);
        return ce.getBytes();
    }

    /**
     * Decodes the command from a CommandDecoder.
     * 
     * @param cd
     */
    public abstract void decode(CommandDecoder cd);

    /**
     * Encode the command for transmission or storage.
     * 
     * @return
     */
    public abstract void encode(CommandEncoder ce);

    /**
     * Fetch the ID of this commandtype. Refer to <code>registerCommand()</code>
     * for details.
     * 
     * @return The ID of the commandtype.
     */
    public abstract CommandType getType();

}