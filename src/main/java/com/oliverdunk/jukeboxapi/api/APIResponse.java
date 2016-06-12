package com.oliverdunk.jukeboxapi.api;

/**
 * Represents possible API responses
 */
public enum APIResponse {

    /**
     * Indicated everything went ok and we expected this response
     */
    SUCCESS,

    /**
     * Indicates that the request was missing arguments
     */
    MISSING_ARGUMENTS,

    /**
     * Indicates that the API key provided was not valid
     */
    INVALID_KEY,

    /**
     * The API request failed for an unknown reason
     * (likely a connection issue or the API is down for maintenance)
     */
    FAILURE
}
