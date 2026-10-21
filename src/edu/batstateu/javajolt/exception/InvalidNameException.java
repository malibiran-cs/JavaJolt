// Handles errors that occur when the user does not enter a name.
package edu.batstateu.javajolt.exception;

public class InvalidNameException extends Exception {

    private static final long serialVersionUID = 1L;

    public InvalidNameException(String message) {
        super(message);
    }
}