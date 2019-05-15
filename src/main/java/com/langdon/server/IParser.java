package com.langdon.server;


import java.io.IOException;
import java.net.ProtocolFamily;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public interface IParser<S,T> {


    /**
     *
     * @return the protocol which the parse belong to
     */
    public Protocol getProtocol();

    public ProtocolFamily getProtocolFamily();

    /**
     *
     * @param s source
     * @return  the target parsed from the source
     * @throws IOException
     */
    public T parse(S s) throws IOException;

    public Charset getCharset();
}
