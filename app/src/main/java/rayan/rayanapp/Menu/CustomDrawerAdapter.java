package rayan.rayanapp.Menu;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import rayan.rayanapp.R;
import rayan.rayanapp.ViewHolders.DrawerItemViewHolder;

public class CustomDrawerAdapter extends ArrayAdapter<DrawerItem> {
    Context context;
    List<DrawerItem> drawerItemList;
    int layoutResID;

    public CustomDrawerAdapter(@NonNull Context context, int resource, @NonNull List<DrawerItem> objects) {
        super(context, resource, objects);
        this.context = context;
        this.drawerItemList = objects;
        this.layoutResID = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        DrawerItemViewHolder drawerHolder;
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            drawerHolder = new DrawerItemViewHolder();

            view = inflater.inflate(layoutResID, parent, false);
            drawerHolder.textView = view.findViewById(R.id.drawer_itemName);
            drawerHolder.icon = view.findViewById(R.id.drawer_icon);

            view.setTag(drawerHolder);

        } else {
            drawerHolder = (DrawerItemViewHolder) view.getTag();
        }
        DrawerItem dItem = (DrawerItem) this.drawerItemList.get(position);

        drawerHolder.icon.setImageDrawable(view.getResources().getDrawable(
                dItem.getImgResID()));
        drawerHolder.textView.setText(dItem.getName());

        return view;
    }
}
