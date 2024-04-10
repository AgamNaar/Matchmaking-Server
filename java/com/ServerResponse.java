package com;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;

/**
 * Custom extension of ResponseEntity for server responses.
 */
public class ServerResponse extends ResponseEntity<String> {
    /**
     * Constructor for creating a server response with just status.
     *
     * @param status The HTTP status code of the response
     */
    public ServerResponse(HttpStatusCode status) {
        super(status);
    }

    /**
     * Constructor for creating a server response with body and status.
     *
     * @param body   The body of the response
     * @param status The HTTP status code of the response
     */
    public ServerResponse(String body, HttpStatusCode status) {
        super(body, status);
    }

    /**
     * Constructor for creating a server response with headers and status.
     *
     * @param headers The headers of the response
     * @param status  The HTTP status code of the response
     */
    public ServerResponse(MultiValueMap<String, String> headers, HttpStatusCode status) {
        super(headers, status);
    }

    /**
     * Constructor for creating a server response with body, headers, and raw status.
     *
     * @param body      The body of the response
     * @param headers   The headers of the response
     * @param rawStatus The raw HTTP status code of the response
     */
    public ServerResponse(String body, MultiValueMap<String, String> headers, int rawStatus) {
        super(body, headers, rawStatus);
    }

    /**
     * Constructor for creating a server response with body, headers, and status.
     *
     * @param body       The body of the response
     * @param headers    The headers of the response
     * @param statusCode The HTTP status code of the response
     */
    public ServerResponse(String body, MultiValueMap<String, String> headers, HttpStatusCode statusCode) {
        super(body, headers, statusCode);
    }
}
