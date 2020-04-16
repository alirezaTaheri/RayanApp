package rayan.rayanapp.ViewHolders;

import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import rayan.rayanapp.Data.Device;
import rayan.rayanapp.Listeners.ScenarioOnActionClickedListener;
import rayan.rayanapp.R;
import rayan.rayanapp.Util.AppConstants;

public class ActionViewHolder1Bridge extends BasicViewHolder<Device, ScenarioOnActionClickedListener<Device>> {
    private final String TAG = ActionViewHolder1Bridge.class.getSimpleName();
    @BindView(R.id.name)
    TextView name;
    @BindView(R.id.pin1)
    ImageView pin1;
    @BindView(R.id.bottomStrip)
    View bottomStrip;
    @BindView(R.id.clickableLayout)
    LinearLayout clickableLayout;
    @BindView(R.id.remove)
    ImageView remove;
    public ActionViewHolder1Bridge(View itemView) {
        super(itemView);
        ButterKnife.bind(this,itemView);
    }

    @Override
    public void onBind(Device item, @Nullable ScenarioOnActionClickedListener<Device> listener) {
        bottomStrip.setBackgroundColor(ContextCompat.getColor(itemView.getContext(), R.color.baseColor2));
        name.setText(item.getName1());
        if (item.getPin1().equals(AppConstants.ON_STATUS)){
            pin1.setImageDrawable(ContextCompat.getDrawable(itemView.getContext(), R.drawable.ic_lamp_on));
            itemView.post(() -> {
                bottomStrip.getLayoutParams().width = itemView.getWidth();
                bottomStrip.requestLayout();
            });
        }
        else {
            pin1.setImageDrawable(ContextCompat.getDrawable(itemView.getContext(), R.drawable.ic_lamp_off));
            bottomStrip.getLayoutParams().width = 0;
            bottomStrip.requestLayout();
        }

        if (listener != null){
            clickableLayout.setOnClickListener(vv -> listener.pin1Clicked(item, getAdapterPosition()));
            remove.setOnClickListener(v -> listener.removeClicked(item, getAdapterPosition()));
        }
    }
}
