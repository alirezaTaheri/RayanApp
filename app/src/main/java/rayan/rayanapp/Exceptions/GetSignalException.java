package rayan.rayanapp.Exceptions;

import rayan.rayanapp.Util.AppConstants;

public class GetSignalException extends RayanException {

    public GetSignalException(String s) {
        switch (s){
            case AppConstants.LEARN_TIMEOUT:
                message = "زمان شما به اتمام رسیده است";
                break;
            case AppConstants.NOT_IN_LEARN_ERROR:
                message = "دستگاه در حالت یادگیری نیست";
                break;
        }
    }

}
