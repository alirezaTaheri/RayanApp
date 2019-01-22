package rayan.rayanapp.MainActivity;

import rayan.rayanapp.Util.BaseRecyclerListener;

public interface OnStatusIconClickListener<T> extends BaseRecyclerListener {
    void onPin1Clicked(T Item);
    void onPin2Clicked(T Item);

}
