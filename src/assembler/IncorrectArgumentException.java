package assembler;

/**
 * Created by Rusty on 11/1/2016.
 */
public class IncorrectArgumentException extends Exception {
    public IncorrectArgumentException() {
        super();
    }
    public IncorrectArgumentException(String message) { super(message); }
}
