package rayan.rayanapp.Util;

import android.content.Context;
import android.util.Log;
import org.apache.commons.net.ftp.FTP;
import java.io.File;
import java.io.FileInputStream;

public class FTPClient {

    public void uploadFile(Context context){
        org.apache.commons.net.ftp.FTPClient con = null;

        try {
            con = new org.apache.commons.net.ftp.FTPClient();
            con.connect("192.168.1.105");

            if (con.login("Administrator", "KUjWbk")) {
                con.enterLocalPassiveMode(); // important!
                con.setFileType(FTP.BINARY_FILE_TYPE);
                String data = context.getFilesDir() +File.separator+ "abc.txt";
                FileInputStream in = new FileInputStream(new File(data));
                boolean result = con.storeFile("/vivekm4a.m4a", in);
                in.close();
                if (result) Log.v("upload result", "succeeded");
                con.logout();
                con.disconnect();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
