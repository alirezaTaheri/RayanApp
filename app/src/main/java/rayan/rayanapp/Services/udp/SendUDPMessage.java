package rayan.rayanapp.Services.udp;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.util.Log;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import rayan.rayanapp.Util.AppConstants;

/**
 * Created by alireza321 on 23/11/2017.
 */

public class SendUDPMessage {
    public static String TAG = SendUDPMessage.class.getSimpleName();
    private AsyncTask<Void, Void, Void> async_cient;
    private int port = AppConstants.UDP_SEND_PORT;
    public SendUDPMessage(){}

    @SuppressLint({"NewApi", "StaticFieldLeak"})
    public void sendUdpMessage(final String address, final String message) {
        async_cient = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                DatagramSocket ds = null;
                try {
                    String address1;
                    address1 = address;
                    if (address.contains("/"))
                    address1  = address.replace("/","");
                    InetAddress addr = InetAddress.getByName(address1);
                    ds = new DatagramSocket();
                    DatagramPacket dp;
                    dp = new DatagramPacket(message.getBytes(), message.length(), addr, port);
                    ds.setBroadcast(true);
                    ds.send(dp);
                    Log.d(TAG,message + "Sent To : "+address1);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (ds != null)
                    {
                        ds.close();
                    }
                }
                return null;
            }
            protected void onPostExecute(Void result) {
                super.onPostExecute(result);
            }
        };
            async_cient.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }}