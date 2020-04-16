package rayan.rayanapp.ViewHolders;

import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import rayan.rayanapp.Fragments.NewRemoteSelectBrandFragment;
import rayan.rayanapp.Listeners.OnRemoteBrandClicked;
import rayan.rayanapp.R;

public class RemoteBrandViewHolder extends BasicViewHolder<String, OnRemoteBrandClicked<String>> {

    @BindView(R.id.name)
    TextView name;
    private final String TAG = "RemoteTypeViewHolder";
    public RemoteBrandViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void bind(String item, OnRemoteBrandClicked<String> listener, boolean hideDivider){
        name.setText(item);
        itemView.setOnClickListener(v -> listener.onRemoteBrandClicked(item, getAdapterPosition()));
        if (((NewRemoteSelectBrandFragment)listener).getSelectedBrand().equals(item))
            itemView.setBackground(ContextCompat.getDrawable(itemView.getContext(), R.drawable.background_selected_item_base_color));
        else itemView.setBackground(null);
    }

    @Override
    public void onBind(String item, @Nullable OnRemoteBrandClicked<String> listener) {
    }

}
