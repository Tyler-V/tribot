package scripts.usa.api.fxgson.adapters.properties;

/**
 * Thrown when a null property is being serialized, and the adapter was not configured to accept null values.
 */
public class NullPropertyException extends RuntimeException {

    public NullPropertyException() {
        super("Null properties are forbidden");
    }
}
