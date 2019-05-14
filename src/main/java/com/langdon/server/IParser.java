package com.langdon.server;


import java.net.ProtocolFamily;

public interface IParser {

    /**
     *
     * @return the protocol which the parse belong to
     */
    public Protocol getProtocol();

    public ProtocolFamily getProtocolFamily();
}
