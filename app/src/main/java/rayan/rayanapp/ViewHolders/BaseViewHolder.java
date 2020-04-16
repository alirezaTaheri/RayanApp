package rayan.rayanapp.ViewHolders;

import android.animation.ValueAnimator;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import rayan.rayanapp.Data.BaseDevice;
import rayan.rayanapp.Data.Device;
import rayan.rayanapp.Listeners.BaseRecyclerListener;
import rayan.rayanapp.Listeners.OnDeviceClickListener;
import rayan.rayanapp.R;

public abstract class BaseViewHolder extends RecyclerView.ViewHolder {
    public BaseViewHolder(View itemView) {
        super(itemView);
    }

    public abstract void onBind(BaseDevice item, @Nullable OnDeviceClickListener<BaseDevice> listener);


    public void changePosition(BaseDevice item, OnDeviceClickListener<BaseDevice> listener){
        Log.d("BaseViewHo", "changePosition() called with: item = [" + item + "], listener = [" + listener + "]");
    }

    public void changeName(String name){
        ((TextView)itemView.findViewById(R.id.name)).setText(name);
    }

    public void ipChanged(OnDeviceClickListener<BaseDevice> listener, Device item){}

    public int getDeviceItemWidth(){
        return itemView.getWidth();
    }
    public void startToggleAnimationPin1(ValueAnimator v){}
    public void stopToggleAnimationPin1(ValueAnimator v, OnDeviceClickListener<BaseDevice> listener, Device item){}
    public void startToggleAnimationPin2(ValueAnimator v){}
    public void stopToggleAnimationPin2(ValueAnimator v, OnDeviceClickListener<BaseDevice> listener, Device item){}
    public void updateBottomStripPin1(int b){}
    public void updateBottomStripPin2(int b){}
    public int getItemWidth(){
        return itemView.getWidth();
    }
}
