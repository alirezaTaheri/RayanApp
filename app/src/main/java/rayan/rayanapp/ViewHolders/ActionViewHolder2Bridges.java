package rayan.rayanapp.ViewHolders;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import rayan.rayanapp.Data.Device;
import rayan.rayanapp.Listeners.OnToggleDeviceListener;
import rayan.rayanapp.Listeners.ScenarioOnActionClickedListener;
import rayan.rayanapp.R;
import rayan.rayanapp.Util.AppConstants;

public class ActionViewHolder2Bridges extends ActionViewHolder1Bridge {
    private final String TAG = ActionViewHolder2Bridges.class.getSimpleName();
    @BindView(R.id.name)
    TextView name;
    @BindView(R.id.pin1)
    ImageView pin1;
    @BindView(R.id.pin2)
    ImageView pin2;
    @BindView(R.id.bottomStrip2)
    View bottomStrip2;
    @BindView(R.id.clickableLayout2)
    LinearLayout clickableLayout2;
    public ActionViewHolder2Bridges(View itemView) {
        super(itemView);
        ButterKnife.bind(this,itemView);
    }

    @Override
    public void onBind(Device item, @Nullable ScenarioOnActionClickedListener<Device> listener) {
        super.onBind(item, listener);
        if (item.getPin2().equals(AppConstants.ON_STATUS)){
            pin2.setImageDrawable(ContextCompat.getDrawable(itemView.getContext(), R.drawable.ic_lamp_on));
            itemView.post(new Runnable() {
                @Override
                public void run(){
                    Log.e("///////////////", "cellWidth: " + itemView.getWidth());
                    bottomStrip2.getLayoutParams().width = itemView.getWidth()/2;
                    bottomStrip2.requestLayout();
                }
            });
        }
        else {
            bottomStrip2.getLayoutParams().width = 0;
            bottomStrip2.requestLayout();
            pin2.setImageDrawable(ContextCompat.getDrawable(itemView.getContext(), R.drawable.ic_lamp_off));
        }
        if (listener != null){
            clickableLayout2.setOnClickListener(v -> listener.pin2Clicked(item, getAdapterPosition()));
        }
    }


}