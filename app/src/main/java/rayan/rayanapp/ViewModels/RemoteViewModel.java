package rayan.rayanapp.ViewModels;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;
import rayan.rayanapp.App.RayanApplication;
import rayan.rayanapp.Data.Button;
import rayan.rayanapp.Data.Remote;
import rayan.rayanapp.Data.RemoteData;
import rayan.rayanapp.Data.RemoteHub;
import rayan.rayanapp.R;
import rayan.rayanapp.Retrofit.ApiService;
import rayan.rayanapp.Retrofit.ApiUtils;
import rayan.rayanapp.Retrofit.remote.MainData;
import rayan.rayanapp.Retrofit.remote.RemoteBrandsResponse;
import rayan.rayanapp.Retrofit.remote.RemoteDataResponse;
import rayan.rayanapp.Retrofit.remotehub.version_1.Models.requests.api.AddRemoteRequest;
import rayan.rayanapp.Retrofit.remotehub.version_1.Models.requests.api.EditRemoteRequest;
import rayan.rayanapp.Retrofit.remotehub.version_1.Models.responses.api.RemoteDatasResponse;
import rayan.rayanapp.Retrofit.remotehub.version_1.Models.responses.api.RemoteHubsResponse;
import rayan.rayanapp.Util.AppConstants;
import rayan.rayanapp.Util.RemoteHub.Communicate.RemoteHubSendData;
import rayan.rayanapp.Util.RemoteHub.Communicate.RemoteHubSendMessage;
import rayan.rayanapp.Util.SingleLiveEvent;

import static android.support.constraint.ConstraintLayout.LayoutParams.PARENT_ID;

public class RemoteViewModel extends AddNewRemoteViewModel {

    public RemoteViewModel(@NonNull Application application) {
        super(application);

    }

    public void addProgressBar(View v, String tag){
        ProgressBar progressBar = new ProgressBar(getApplication());
        progressBar.setTag(tag+"_learned");
        progressBar.getIndeterminateDrawable().setColorFilter(Color.WHITE, android.graphics.PorterDuff.Mode.MULTIPLY);
        progressBar.setTranslationY(10 * getApplication().getResources().getDisplayMetrics().density);
        progressBar.setTranslationX(-10 * getApplication().getResources().getDisplayMetrics().density);
        ConstraintLayout layout = v.findViewWithTag(tag);
        ConstraintLayout.LayoutParams lp =
                new ConstraintLayout.LayoutParams(Math.round(15 * getApplication().getResources().getDisplayMetrics().density),
                        Math.round(15 * getApplication().getResources().getDisplayMetrics().density));
        lp.topToTop = PARENT_ID;
        lp.rightToRight = PARENT_ID;
        layout.addView(progressBar, lp);
    }
    public void removeProgressbar(String tag, ViewGroup parent){
        for (int a=0; a<parent.getChildCount();a++)
            if (parent.getChildAt(a).getTag()!=null&&parent.getChildAt(a).getTag().equals(tag))
                if (parent.getChildAt(a) instanceof ViewGroup)
                    removeProgressbar(tag+"_learned", ((ViewGroup)parent.getChildAt(a)));
                else
                    parent.removeViewAt(a);

    }
}
