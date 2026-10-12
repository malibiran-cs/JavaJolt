package edu.batstateu.javajolt.exception;

public class SaveFileException extends Exception{
    private static final long serialVersionUID = 1L;

    public SaveFileException(String message) {
        super(message);
    }

    public SaveFileException(String message, Throwable cause) {
        super(message, cause);
    }
}
