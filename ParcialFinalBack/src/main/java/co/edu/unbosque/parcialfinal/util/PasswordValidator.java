package co.edu.unbosque.parcialfinal.util;

import co.edu.unbosque.parcialfinal.exception.PasswordNotValidException;
import java.util.regex.Pattern;

public class PasswordValidator {

  private static final Pattern POLICY =
      Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9])\\S{8,64}$");

  private PasswordValidator() {}

  public static boolean isValid(String password) {
    return password != null && POLICY.matcher(password).matches();
  }

  public static void validate(String password) throws PasswordNotValidException {
    if (!isValid(password)) {
      throw new PasswordNotValidException();
    }
  }
}
