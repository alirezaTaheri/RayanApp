package rayan.rayanapp.Fragments;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import rayan.rayanapp.Activities.AddNewRemoteActivity;
import rayan.rayanapp.Adapters.recyclerView.RemoteBrandsRecyclerViewAdapter;
import rayan.rayanapp.Adapters.recyclerView.RemoteTypesRecyclerViewAdapter;
import rayan.rayanapp.Listeners.AddNewRemoteNavListener;
import rayan.rayanapp.Listeners.OnRemoteBrandClicked;
import rayan.rayanapp.R;
import rayan.rayanapp.ViewModels.AddNewRemoteViewModel;


public class NewRemoteSelectBrandFragment extends Fragment implements OnRemoteBrandClicked<String>, AddNewRemoteNavListener {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    RemoteBrandsRecyclerViewAdapter recyclerViewAdapter;
    private AddNewRemoteViewModel mViewModel;
    private List<String> allBrands = new ArrayList<>();
    private List<String> allBrands2 = new ArrayList<>();
    @BindView(R.id.searchView)
    SearchView searchView;
    AddNewRemoteActivity activity;
    private Pair<String, Integer> selectedBrand = new Pair<>("",-1);
    public static NewRemoteSelectBrandFragment newInstance(String selectedType) {
        Bundle b = new Bundle();
        b.putString("selectedType", selectedType);
        NewRemoteSelectBrandFragment selectBrandFragment = new NewRemoteSelectBrandFragment();
        selectBrandFragment.setArguments(b);
        return selectBrandFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.new_remote_select_brand_fragment, container, false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getActivity().getWindow();
            window.setStatusBarColor(ContextCompat.getColor(getContext(), R.color.grey_light_4));
//            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
//            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }
        ButterKnife.bind(this, v);
//        ((AutoCompleteTextView) searchView.findViewById(R.id.search_src_text)).setBackgroundColor(ContextCompat.getColor(getContext(), R.color.grey_light_4));;
//        searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text).setBackgroundColor(ContextCompat.getColor(getContext(), R.color.grey_light_4));
        return v;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(AddNewRemoteViewModel.class);
        allBrands = mViewModel.getAllBrands();
        allBrands2 = mViewModel.getAllBrands();
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerViewAdapter = new RemoteBrandsRecyclerViewAdapter(getActivity(), allBrands);
        recyclerViewAdapter.setListener(this);
        recyclerView.setAdapter(recyclerViewAdapter);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                Log.e("Submit Clicked For:", "TEXT: " + s);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                Log.e("Search for: ", "TEXT: " + s);
                recyclerViewAdapter.filter(s, allBrands2);
                return true;
            }
        });
    }

    @Override
    public void onRemoteBrandClicked(String item, int position) {
        recyclerViewAdapter.notifyItemChanged(selectedBrand.second);
        selectedBrand = new Pair<>(item, position);
        recyclerViewAdapter.notifyItemChanged(selectedBrand.second);
    }

    public String getSelectedBrand() {
        return selectedBrand.first;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (AddNewRemoteActivity) context;
    }

    @Override
    public void onBackClicked() {
        activity.onBackPressed();
    }

    @Override
    public void goToNextStep(Map<String, String> data) {
        Map<String, String> map = new HashMap<>();
        map.put("brand", selectedBrand.first);
        activity.doOnNext(map);
    }

    @Override
    public void verifyStatus() {
        if (!selectedBrand.first.equals(""))
            goToNextStep(null);
    }

}
