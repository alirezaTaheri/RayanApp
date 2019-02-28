package rayan.rayanapp.Fragments;

import android.app.AlertDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import rayan.rayanapp.Adapters.recyclerView.GroupsRecyclerViewAdapter;
import rayan.rayanapp.Listeners.OnGroupClicked;
import rayan.rayanapp.R;
import rayan.rayanapp.Retrofit.Models.Responses.api.Group;
import rayan.rayanapp.ViewModels.GroupsListFragmentViewModel;

public class GroupsListFragment extends Fragment implements OnGroupClicked<Group> {

    GroupsRecyclerViewAdapter groupsRecyclerViewAdapter;
    GroupsListFragmentViewModel groupsListFragmentViewModel;

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    List<Group> groups = new ArrayList<>();
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
        groupsRecyclerViewAdapter = new GroupsRecyclerViewAdapter(getActivity(), groups);
        groupsRecyclerViewAdapter.setListener(this);
        groupsListFragmentViewModel = ViewModelProviders.of(this).get(GroupsListFragmentViewModel.class);
        groupsListFragmentViewModel.getAllGroupsLive().observe(Objects.requireNonNull(getActivity()), groups -> {
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
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(getActivity(), android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(getActivity());
        }
        builder
                .setMessage("آیا میخواهید این گروه را حذف کنید؟")
                .setPositiveButton("بله", (dialog, which) -> {
                    groupsListFragmentViewModel.deleteGroup(Item.getId()).observe(this, baseResponse -> {
                        if (baseResponse.getStatus().getCode().equals("404") && baseResponse.getData().getMessage().equals("User not found")){
                            Toast.makeText(getActivity(), "این گروه وجود ندارد", Toast.LENGTH_SHORT).show();
                        }
                        else if (baseResponse.getStatus().getCode().equals("403") && baseResponse.getData().getMessage().equals("Repeated")){
                            Toast.makeText(getActivity(), "شما قادر به حذف این گروه نیستید", Toast.LENGTH_SHORT).show();
                        }
                        else if (baseResponse.getStatus().getCode().equals("204")){
                            Toast.makeText(getActivity(), "گروه با موفقیت حذف شد", Toast.LENGTH_SHORT).show();
                            groupsListFragmentViewModel.getGroups();
                        }
                        else
                            Toast.makeText(getActivity(), "مشکلی وجود دارد", Toast.LENGTH_SHORT).show();
                    });
                })
                .show();
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
}
