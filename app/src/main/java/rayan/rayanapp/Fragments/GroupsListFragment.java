package rayan.rayanapp.Fragments;


import androidx.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.core.content.pm.ShortcutInfoCompat;
import androidx.core.content.pm.ShortcutManagerCompat;
import androidx.core.graphics.drawable.IconCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rayan.rayanapp.Activities.GroupsActivity;
import rayan.rayanapp.Activities.MainActivity;
import rayan.rayanapp.Adapters.recyclerView.GroupsRecyclerViewAdapter;
import rayan.rayanapp.Listeners.OnGroupClicked;
import rayan.rayanapp.R;
import rayan.rayanapp.Retrofit.switches.version_1.Models.Responses.api.Group;
import rayan.rayanapp.ViewModels.GroupsListFragmentViewModel;

public class GroupsListFragment extends Fragment implements OnGroupClicked<Group> {
    private final String TAG = CreateGroupFragment.class.getSimpleName();
//    OnToolbarNameChange onToolbarNameChange;
    GroupsRecyclerViewAdapter groupsRecyclerViewAdapter;
    GroupsListFragmentViewModel groupsListFragmentViewModel;
    String groupId;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    List<Group> groups = new ArrayList<>();
    private static GroupsListFragment instance = null;
    public GroupsListFragment() {
        // Required empty public constructor
    }

    public static GroupsListFragment newInstance() {
        GroupsListFragment fragment = new GroupsListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
        groupsRecyclerViewAdapter = new GroupsRecyclerViewAdapter(getActivity(), groups);
        groupsRecyclerViewAdapter.setListener(this);
        groupsListFragmentViewModel = ViewModelProviders.of(this).get(GroupsListFragmentViewModel.class);
        groupsListFragmentViewModel.getAllGroupsLive().observe(Objects.requireNonNull(getActivity()), groups -> {
            groupsRecyclerViewAdapter.updateItems(groups);
        });

//       onToolbarNameChange=(OnToolbarNameChange)getActivity();
//        onToolbarNameChange.toolbarNameChanged("گروه\u200cها");

        ((GroupsActivity) clickOnGroup).toolbarNameChanged("گروه\u200cها");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_groups_list, container, false);
        ButterKnife.bind(this, view);

        if (isTablet(getActivity())) {
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
        } else {
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
        }
        recyclerView.setItemViewCacheSize(100);
        recyclerView.setAdapter(groupsRecyclerViewAdapter);
        return view;
    }

    @Override
    public void onGroupClicked(Group item) {
        clickOnGroup.clickOnGroup(item);
    }

    @OnClick(R.id.createGroup)
    void createGroup(){
        clickOnGroup.createGroup();
    }
    public interface ClickOnGroup{
        void clickOnGroup(Group group);
        void createGroup();
    }
    ClickOnGroup clickOnGroup;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ClickOnGroup) {
            clickOnGroup = (ClickOnGroup) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        clickOnGroup = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        groupsListFragmentViewModel.getGroups();
     //   onToolbarNameChange.toolbarNameChanged("گروه\u200cها");

        ((GroupsActivity) getActivity()).toolbarNameChanged("گروه\u200cها");
    }


    public static GroupsListFragment getInstance() {
        return instance;
    }
    public static int calculateNoOfColumns(Context context, float columnWidthDp) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float screenWidthDp = displayMetrics.widthPixels / displayMetrics.density;
        int noOfColumns = (int) (screenWidthDp / columnWidthDp + 0.5); // +0.5 for correct rounding to int.
        return noOfColumns;
    }

    public static boolean isTablet(Context ctx){
        return (ctx.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

//add shortcut code
    private static final String DATA = "tanelikorri://shortcut";
    public void onInstallShortcutClick() {
        // Get the shortcut intent
        final Intent sIntent = new Intent(getContext(), MainActivity.class);
        sIntent.setAction(Intent.ACTION_MAIN);
        // Include data to know when the app is started from the shortcut
        sIntent.setData(Uri.parse(DATA));

        if (ShortcutManagerCompat.isRequestPinShortcutSupported(getContext())) {
            ShortcutInfoCompat shortcut = new ShortcutInfoCompat.Builder(getContext(), "id")
                    // Shortcut label
                    .setShortLabel("Example shortcut")
                    .setAlwaysBadged()
                    .setIcon(IconCompat.createWithResource(getContext(), R.mipmap.ic_launcher))
                    .setIntent(sIntent)
                    .build();
            ShortcutManagerCompat.requestPinShortcut(getContext(), shortcut, null);
        } else {
            Toast.makeText(getContext(), R.string.shortcut_not_supported, Toast.LENGTH_LONG).show();
        }
    }

}
