package com.jshaak.armadacombatlog;

/**
 * Created by Jordan Shaak on 9/14/2017.
 */

public class IncompleteFormException extends Exception {
    private String message;
    public IncompleteFormException(String message)
    {
        this.message = message;
    }
    public String getMessage()
    {
        return message;
    }
}
