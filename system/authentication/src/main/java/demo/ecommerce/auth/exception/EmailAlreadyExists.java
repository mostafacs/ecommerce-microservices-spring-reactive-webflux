package demo.ecommerce.auth.exception;

/**
 * @author Mostafa Albana
 */
public class EmailAlreadyExists extends Exception {
    @Override
    public String getMessage() {
        return "This email already exists";
    }
}
