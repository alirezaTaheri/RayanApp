package rayan.rayanapp.Util;

import rayan.rayanapp.Exceptions.RayanException;

public class LiveResponse {
    boolean successful;
    private String message;
    private RayanException exception;

    public LiveResponse(boolean successful, String message, RayanException exception) {
        this.successful = successful;
        this.message = message;
        this.exception = exception;
        if (message == null)
            this.message = exception.getExceptionType();
    }

    public boolean isSuccessful() {
        return successful;
    }

    public String getMessage() {
        return message;
    }

    public RayanException getException() {
        return exception;
    }

    @Override
    public String toString() {
        return "LiveResponse{" +
                "successful=" + successful +
                ", message='" + message + '\'' +
                ", exception=" + exception +
                '}';
    }
}
