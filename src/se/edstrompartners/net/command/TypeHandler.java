package se.edstrompartners.net.command;

public interface TypeHandler<T> {
    public CommandEncoder encode(CommandEncoder ce, T val);

    public T decode(CommandDecoder cd);

}
