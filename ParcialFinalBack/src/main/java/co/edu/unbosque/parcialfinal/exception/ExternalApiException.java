package co.edu.unbosque.parcialfinal.exception;

public class ExternalApiException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  private final int status;

  public ExternalApiException(String message, int status) {
    super(message);
    this.status = status;
  }

  public ExternalApiException(String message, Throwable cause) {
    super(message, cause);
    this.status = 502;
  }

  public int getStatus() {
    return status;
  }
}
