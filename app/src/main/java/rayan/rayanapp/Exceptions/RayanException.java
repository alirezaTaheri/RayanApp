package rayan.rayanapp.Exceptions;

import rayan.rayanapp.Util.AppConstants;

public class RayanException extends RuntimeException {

    protected String message;
    private String exceptionType;
    public RayanException() {}

    public RayanException(String s) {
        exceptionType = s;
        switch (s){
            case AppConstants.TIME_OUT_EXCEPTION:
                message = "در مدت زمان مشخص پاسخ دریافت نشد";
                break;
            case AppConstants.SOCKET_TIME_OUT:
                message = "در مدت زمان مشخص پاسخ دریافت نشد";
                break;
            case AppConstants.UNKNOWN_HOST_EXCEPTION:
                message = "از اتصال خود به شبکه مطمدن شوید";
                break;
            case AppConstants.UNKNOWN_EXCEPTION:
                message = "خطا نامشخصی رخ داد";
                break;
        }
    }
    @Override
    public String getMessage() {
        return message;
    }

    public String getExceptionType() {
        return exceptionType;
    }
}
