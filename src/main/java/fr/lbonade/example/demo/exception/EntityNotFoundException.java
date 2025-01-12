package fr.lbonade.example.demo.exception;

public class EntityNotFoundException extends RuntimeException{

    public static final MessageFormaters MESSAGE_FORMATER =  (String entityName, String id) -> String.format("L'entité %s d'id %s n'existe pas", entityName, id);
    public EntityNotFoundException(String message) {
        super(message);
    }

    public EntityNotFoundException(String entityName, String id) {
        super(MESSAGE_FORMATER.format(entityName, id));
    }
}
