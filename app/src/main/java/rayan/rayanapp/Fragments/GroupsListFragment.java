package rayan.rayanapp.Fragments;


import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rayan.rayanapp.Adapters.recyclerView.GroupsRecyclerViewAdapter;
import rayan.rayanapp.Listeners.OnGroupClicked;
import rayan.rayanapp.R;
import rayan.rayanapp.Retrofit.Models.Responses.api.Group;
import rayan.rayanapp.Util.SnackBarSetup;
import rayan.rayanapp.ViewModels.GroupsListFragmentViewModel;

public class GroupsListFragment extends Fragment implements OnGroupClicked<Group> {
   // YesNoButtomSheetFragment yesNoButtomSheetFragment;
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
        groupsListFragmentViewModel.getAllGroups().observe(Objects.requireNonNull(getActivity()), groups -> {
            groupsRecyclerViewAdapter.updateItems(groups);
        });


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_groups_list, container, false);
        ButterKnife.bind(this, view);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerView.setAdapter(groupsRecyclerViewAdapter);
        return view;
    }

    @Override
    public void onGroupClicked(Group item) {
        clickOnGroup.clickOnGroup(item);
    }

    @Override
    public void onGroupLongPress(Group Item) {
       YesNoButtomSheetFragment bottomSheetFragment = new YesNoButtomSheetFragment().groupListInstance("GroupsListFragment","حذف گروه", "بازگشت", "آیا مایل به حذف این گروه هستید؟",Item.getId());
       bottomSheetFragment.show(getActivity().getSupportFragmentManager(), bottomSheetFragment.getTag());
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
    }

    public void clickOnSubmit(String groupId) {
        Log.e("itemid",groupId);
            groupsListFragmentViewModel.deleteGroup(groupId).observe(this, baseResponse -> {
                Log.e("baseResponse",baseResponse.getStatus().getCode());
                if (baseResponse.getStatus().getCode().equals("404")){
                    SnackBarSetup.snackBarSetup(getActivity().findViewById(android.R.id.content),"این گروه وجود ندارد");
                }
                else if (baseResponse.getStatus().getCode().equals("403")){
                    SnackBarSetup.snackBarSetup(getActivity().findViewById(android.R.id.content),"شما قادر به حذف این گروه نیستید");

                }
                else if (baseResponse.getStatus().getCode().equals("204")){
                    SnackBarSetup.snackBarSetup(getActivity().findViewById(android.R.id.content),"گروه با موفقیت حذف شد");
                    groupsListFragmentViewModel.getGroups();
                }
                else
                    SnackBarSetup.snackBarSetup(getActivity().findViewById(android.R.id.content),"مشکلی وجود دارد");
            });
    }
    public static GroupsListFragment getInstance() {
        return instance;
    }
}
