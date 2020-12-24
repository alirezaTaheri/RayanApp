package rayan.rayanapp.Exceptions;

import rayan.rayanapp.Util.AppConstants;

public class EnterLearnException extends RayanException {

    public EnterLearnException(String s) {
        switch (s){
            case AppConstants.FULL_LEARN_ERROR:
                message = "شخص دیگری درحال انجام این فرآیند است و این امکان برای شما وجود ندارد";
                break;
            case AppConstants.SETTINGS_MODE_ERROR:
                message = "دستگاه در حالت تنظیمات است و قادر به انجام اینکار نیست";
                break;
        }
    }

}
