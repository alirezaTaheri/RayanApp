package rayan.rayanapp.Util;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import org.apache.commons.net.ftp.FTP;
import java.io.File;
import java.io.FileInputStream;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class FTPClient {

    Observable<Object> uploadObservable;
    public void uploadFile(Context context, String ip, String username, String password){
        uploadObservable = Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(ObservableEmitter<Object> e) throws Exception {
                org.apache.commons.net.ftp.FTPClient con = null;
                try {
                    con = new org.apache.commons.net.ftp.FTPClient();
                    e.onNext("Connecting To: " + ip);
                    con.connect("192.168.1.109", AppConstants.FTP_PORT);
                    e.onNext("Connected To: " + ip);
                    if (con.login(username, password)) {
                        con.enterLocalPassiveMode(); // important!
                        e.onNext("Entered Passive Mode");
                        con.setFileType(FTP.BINARY_FILE_TYPE);
                        String data = Environment.getExternalStorageDirectory() +File.separator+ "abc.txt";
                        FileInputStream in = new FileInputStream(new File(data));
                        e.onNext("Stream is: " + in);
                        boolean result = con.storeFile("abc.txt",in);
                        e.onNext("Results isabc: " + result);
                        in.close();
                        if (result) {
                            Log.v("upload result", "succeeded abc");
                            e.onNext("succeeded abc");
                        }else e.onNext("failed abc");
                        String data2 = Environment.getExternalStorageDirectory() +File.separator+ "def.txt";
                        FileInputStream in2 = new FileInputStream(new File(data2));
                        e.onNext("Stream is def: " + in2);
                        boolean result2 = con.storeFile("def.txt",in2);
                        e.onNext("Results is def: " + result2);
                        in.close();
                        if (result2) {
                            Log.v("upload result", "succeeded def");
                            e.onNext("succeeded def");
                        }else e.onNext("failed def");
                        con.logout();
                        con.disconnect();
                        e.onComplete();
                    }
                    else e.onNext("Login Failed");
                }
                catch (Exception error)
                {
                    error.printStackTrace();
                }
            }
        });
        uploadObservable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Object>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.e("><><><><><><><","Going To Subscribe");
            }

            @Override
            public void onNext(Object o) {
                Log.e("><><><><><><><","OnNext: " + o);
            }

            @Override
            public void onError(Throwable e) {
                Log.e("><><><><><><><","Error: " + e);
                e.printStackTrace();
            }

            @Override
            public void onComplete() {
                Toast.makeText(context, "Files Uploaded Successfully", Toast.LENGTH_SHORT).show();
                Log.e("><><><><><><><","Task Completed");
            }
        });
    }
}
