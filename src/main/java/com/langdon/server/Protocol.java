package com.langdon.server;

/**
 * @see  "https://tools.ietf.org/html/rfc791"
 */
public enum  Protocol {

    HTTP,
    DNS,
    FTP,
    SMTP,
    POP;

     @Override
     public String toString(){
         return this.name();
     }

}
