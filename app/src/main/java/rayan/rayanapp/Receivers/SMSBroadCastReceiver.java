package rayan.rayanapp.Receivers;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.SmsMessage;
import android.util.Log;

import java.util.Arrays;

public class SMSBroadCastReceiver extends BroadcastReceiver
{

    @Override
    public void onReceive(Context context, Intent intent)
    {
        // Get Bundle object contained in the SMS intent passed in
        Bundle bundle = intent.getExtras();
        SmsMessage[] smsm = null;
        String sms_str ="";

        if (bundle != null)
        {
            // Get the SMS message
            Object[] pdus = (Object[]) bundle.get("pdus");
            smsm = new SmsMessage[pdus.length];
            for (int i=0; i<smsm.length; i++){
                if (smsm[i].getOriginatingAddress().equals("+9810007119")){

                smsm[i] = SmsMessage.createFromPdu((byte[])pdus[i]);
                    sms_str = smsm[i].getMessageBody();
                    sms_str=sms_str.replaceAll("\\D+","");
                    int icode = Integer.parseInt(sms_str);
                    Log.e("smscode",""+icode + sms_str);
                //Check here sender is yours
                Intent smsIntent = new Intent("otp");
                smsIntent.putExtra("message",sms_str);

                LocalBroadcastManager.getInstance(context).sendBroadcast(smsIntent);

                }
            }
        }
    }
}