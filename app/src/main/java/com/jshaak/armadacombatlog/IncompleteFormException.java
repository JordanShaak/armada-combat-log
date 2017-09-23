package com.jshaak.armadacombatlog;

/**
 * Simple exception class to return a string. May be expanded for future functionality, so I created a custom one.
 */

class IncompleteFormException extends Exception {
    private final String message;
    IncompleteFormException(String message)
    {
        this.message = message;
    }
    public String getMessage()
    {
        return message;
    }
}
