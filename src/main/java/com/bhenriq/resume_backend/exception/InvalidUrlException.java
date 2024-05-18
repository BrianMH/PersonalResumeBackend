package com.bhenriq.resume_backend.exception;

import jakarta.servlet.http.HttpServletResponse;

import java.util.Map;

/**
 * Very similar to all the other exceptions, except this one in particular can take in an array of String/String
 * mappings of invalid URLs and reports them as invalid in the response message. The provided message then functions
 * as the prefix for the error.
 */
public class InvalidUrlException extends RestRuntimeException {
    public InvalidUrlException(String message, Map<String, String> invalidUrls) {
        super(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, constructMessage(message, invalidUrls));
    }

    /**
     * Builds a message of the form
     *      MESSAGE PREFIX:
     *          INVALID KEY 1 : INVALID VALUE 1
     *          INVALID KEY 2 : INVALID VALUE 2
     *          ...
     * @param messagePrefix the message indicator to send
     * @param invalidUrls the collection of key/value pairs making up the invalid url requests
     * @return a message according to the format specified above.
     */
    private static String constructMessage(String messagePrefix, Map<String, String> invalidUrls) {
        StringBuilder respMessage = new StringBuilder(messagePrefix);
        for(Map.Entry<String, String> entry : invalidUrls.entrySet()) {
            respMessage.append(String.format("\n\t%s : %s", entry.getKey(), entry.getValue()));
        }

        return respMessage.toString();
    }
}
