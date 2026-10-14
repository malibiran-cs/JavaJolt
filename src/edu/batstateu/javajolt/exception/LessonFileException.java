package edu.batstateu.javajolt.exception;

public class LessonFileException extends Exception {
    private static final long serialVersionUID = 1L;

    public LessonFileException(String message) {
        super(message);
    }

    public LessonFileException(String message, Throwable cause) {
        super(message, cause);
    }
}
