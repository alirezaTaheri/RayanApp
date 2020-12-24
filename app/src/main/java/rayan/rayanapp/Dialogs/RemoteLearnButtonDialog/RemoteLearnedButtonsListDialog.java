package rayan.rayanapp.Dialogs.RemoteLearnButtonDialog;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.disposables.Disposable;
import rayan.rayanapp.Activities.AddNewRemoteActivity;
import rayan.rayanapp.Data.Button;
import rayan.rayanapp.Data.Remote;
import rayan.rayanapp.Data.RemoteHub;
import rayan.rayanapp.Fragments.NewRemoteControlBase;
import rayan.rayanapp.Listeners.LearnedButtonClickListener;
import rayan.rayanapp.Listeners.RemoteLearnButtonDialogListener;
import rayan.rayanapp.Listeners.RemoteLearnNavigation;
import rayan.rayanapp.R;

public class RemoteLearnedButtonsListDialog extends DialogFragment implements LearnedButtonClickListener {


    public static RemoteLearnedButtonsListDialog newInstance(ArrayList<Button> buttons) {
        RemoteLearnedButtonsListDialog dialog = new RemoteLearnedButtonsListDialog();
        Bundle b = new Bundle();
        b.putParcelableArrayList("buttons", buttons);
        dialog.setArguments(b);
        return dialog;
    }
    private NewRemoteControlBase controlBaseFragment;
    private AddNewRemoteActivity activity;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.emptyRecyclerView)
    TextView emptyRecyclerView;
    private final String TAG = "RemoteLearnedButtonsListDialog";
    private List<Button> buttons = new ArrayList<>();
    LearnedButtonsAdapter adapter;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        controlBaseFragment = (NewRemoteControlBase) getParentFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@android.support.annotation.NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.remote_learned_buttons_list_dialog, container, false);
        buttons = getArguments().getParcelableArrayList("buttons");
        ButterKnife.bind(this, v);
        adapter = new LearnedButtonsAdapter(buttons, this);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setItemAnimator(null);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        if (buttons.size()==0)
            emptyRecyclerView.setVisibility(View.VISIBLE);
        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (AddNewRemoteActivity) context;
    }

    @OnClick(R.id.confirm)
    public void confirm(){
        Map<String, Button> buttonMap = new HashMap<>();
        for (Button b: buttons)
            buttonMap.put(b.getName(), b);
        controlBaseFragment.setButtonMap(buttonMap);
        dismiss();
    }
    @OnClick(R.id.cancel)
    public void cancel(){
        dismiss();
    }

    @Override
    public void onDeleteClicked(Button button, int position) {
        buttons.remove(position);
        adapter.notifyItemRemoved(position);
        if (buttons.size()==0)
            emptyRecyclerView.setVisibility(View.VISIBLE);

    }

    public class LearnedButtonsAdapter extends RecyclerView.Adapter<LearnedButtonViewHolder>{
        public LearnedButtonsAdapter(List<Button> buttons, LearnedButtonClickListener listener) {
            this.buttons = buttons;
            this.listener = listener;
        }
        LearnedButtonClickListener listener;
        private List<Button> buttons = new ArrayList<>();
        @NonNull
        @Override
        public LearnedButtonViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            return new LearnedButtonViewHolder(LayoutInflater.from(activity).inflate(R.layout.item_learned_button, viewGroup, false), listener);
        }

        @Override
        public void onBindViewHolder(@NonNull LearnedButtonViewHolder viewHolder, int i) {
            viewHolder.bind(buttons.get(i));
        }

        @Override
        public int getItemCount() {
            return buttons.size();
        }
    }
    public class LearnedButtonViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.name)
        TextView name;
        @BindView(R.id.delete)
        ImageView delete;
        LearnedButtonClickListener listener;
        public LearnedButtonViewHolder(@NonNull View itemView, LearnedButtonClickListener listener) {
            super(itemView);
            this.listener = listener;
        }

        public void bind(Button button){
            ButterKnife.bind(this, itemView);
            name.setText(button.getName());
            delete.setOnClickListener(v -> listener.onDeleteClicked(button, getAdapterPosition()));
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        getDialog().setCanceledOnTouchOutside(false);
        getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }
}
