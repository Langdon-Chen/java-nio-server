package com.langdon.http;


public interface HttpResponse extends HttpMessage {

    /**
     * Returns the HTTP Status Code of this response.
     *
     * @see   "http://tools.ietf.org/html/rfc2616.html#section-6.1.1"

     */
    HttpStatus getHttpStatus();

}
