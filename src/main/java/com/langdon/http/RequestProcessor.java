package com.langdon.http;

import com.langdon.http.basic.HttpMethod;
import com.langdon.http.basic.ServerHttpRequest;
import com.langdon.http.basic.ServerHttpResponse;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * process the body alternatively base on headers
 */
public class RequestProcessor  {
    public RequestProcessor(){}


    ServerHttpResponse process(ServerHttpRequest request){
        ServerHttpResponse response = null;
        final HttpMethod method = request.getMethod();
        switch (method){
            case HEAD:
            case GET:
            case POST:
                if (isMultipart(request)){

                    break;
                }
            case PUT:
            case DELETE:
            case PATCH:
                break;
        }

        return response;
    }

    /**
     * Checks if is multipart.
     * @return true, if it is multipart
     */
    public boolean isMultipart(ServerHttpRequest request) {
        String contentType = request.getHeaders().get("Content-Type");
        if (contentType == null || !contentType.contains("multipart/form-data")) {
            return false;
        }
        return true;
    }

    /**
     * The Class ContentDisposition.
     */
    public static class ContentDisposition {

        /** The Constant DISPOSITION_FORM_EXPRESSION. */
        private static final String DISPOSITION_FORM_EXPRESSION =
                "Content-Disposition: form-data; name=\".*\"; filename=\"(.*){1}\".*";

        /** The Constant DISPOSITION_PATTERN. */
        private  final Pattern DISPOSITION_PATTERN = Pattern.compile(DISPOSITION_FORM_EXPRESSION);

        /** The file name. */
        private String fileName = null;

        private static final String [] inputNames = {
                "file","submit"
        };

        /**
         * Instantiates a new content disposition.
         * @param line the line
         */
        public ContentDisposition(String line) {
            Matcher matcher = DISPOSITION_PATTERN.matcher(line);
            if (matcher.matches()) {
                fileName = matcher.group(1);
            }
        }

        /**
         * Gets the file name.
         * @return the file name
         */
        public String getFileName() {
            return fileName;
        }

        /**
         * Sets the file name.
         * @param fileName the new file name
         */
        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

    }
}
