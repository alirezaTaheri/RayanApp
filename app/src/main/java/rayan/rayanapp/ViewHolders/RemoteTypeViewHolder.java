package rayan.rayanapp.ViewHolders;

import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import rayan.rayanapp.Fragments.NewRemoteSelectTypeFragment;
import rayan.rayanapp.Listeners.OnRemoteTypeClicked;
import rayan.rayanapp.R;

public class RemoteTypeViewHolder extends BasicViewHolder<String, OnRemoteTypeClicked<String>> {

    @BindView(R.id.name)
    TextView name;
    @BindView(R.id.icon)
    ImageView icon;
    private final String TAG = "RemoteTypeViewHolder";
    public RemoteTypeViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void bind(String item, OnRemoteTypeClicked<String> listener, boolean hideDivider){
        if (item.equals("TV")){
            name.setText("تلویزیون");
            icon.setImageDrawable(ContextCompat.getDrawable(itemView.getContext(),R.drawable.ic_tv));
        } else if(item.equals("AC")){
            name.setText("کولرگازی");
            icon.setImageDrawable(ContextCompat.getDrawable(itemView.getContext(),R.drawable.ic_air_conditioner));
        }else if (item.equals("Learn")){
            name.setText("ساختن ریموت دلخواه");
            icon.setImageDrawable(ContextCompat.getDrawable(itemView.getContext(),R.drawable.ic_add_2));
        }
        itemView.setOnClickListener(v -> listener.onRemoteTypeClicked(item, getAdapterPosition()));
        if (((NewRemoteSelectTypeFragment)listener).getSelectedType().equals(item))
            itemView.setBackground(ContextCompat.getDrawable(itemView.getContext(), R.drawable.new_remote_selected_type_backgrand));
        else itemView.setBackground(null);
    }

    @Override
    public void onBind(String item, @Nullable OnRemoteTypeClicked<String> listener) {
    }

}
