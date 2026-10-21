package edu.batstateu.javajolt.exception; //Angelo

// Thrown when a topic and difficulty combination has no questions written yet
public class NoQuestionsException extends Exception {
    private static final long serialVersionUID = 1L;

    public NoQuestionsException(String message) {
        super(message);
    }
}
