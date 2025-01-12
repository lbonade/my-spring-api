package fr.lbonade.example.demo.exception;

public class ConcurrentModificationException extends RuntimeException {

    public static final MessageFormaters MESSAGE_FORMATER =  (String entityName, String id) -> String.format("L'entité %s d'id %s à été modifiée par quelqu'un d'autre", entityName, id);

    public ConcurrentModificationException(String message) {
        super(message);
    }

    public ConcurrentModificationException(String entityName, String id) {
        super(MESSAGE_FORMATER.format(entityName, id));
    }
}
