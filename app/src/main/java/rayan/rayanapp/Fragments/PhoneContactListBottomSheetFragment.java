package rayan.rayanapp.Fragments;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.TextInputEditText;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import rayan.rayanapp.Adapters.recyclerView.PhoneContactsRecyclerViewAdapter;
import rayan.rayanapp.App.RayanApplication;
import rayan.rayanapp.Data.PhoneContact;
import rayan.rayanapp.Listeners.OnAddUserToGroupSubmitClicked;
import rayan.rayanapp.Listeners.OnBottomSheetSubmitClicked;
import rayan.rayanapp.R;
import rayan.rayanapp.ViewModels.phoneContactListFragmentViewModel;

public class PhoneContactListBottomSheetFragment extends BottomSheetDialogFragment {
private OnAddUserToGroupSubmitClicked onAddUserToGroupSubmitClicked;
public void setOnAddUserToGroupSubmitClicked(OnAddUserToGroupSubmitClicked onAddUserToGroupSubmitClicked){
   this.onAddUserToGroupSubmitClicked=onAddUserToGroupSubmitClicked;
}
    ArrayList<PhoneContact> phoneContacts = new ArrayList<>();
    @BindView(R.id.phoneContactsRecyclerView)
    RecyclerView phoneContactsRecyclerView;
    PhoneContactsRecyclerViewAdapter phoneContactsRecyclerViewAdapter;
    private String tag;
    public static PhoneContactListBottomSheetFragment newInstance( String tag ) {
        final PhoneContactListBottomSheetFragment fragment= new PhoneContactListBottomSheetFragment();
        final Bundle args = new Bundle();
        args.putString("TAG",tag);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_phone_contact_list, container, false);
        ButterKnife.bind(this, view);
        phoneContactsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        phoneContactsRecyclerView.setAdapter(phoneContactsRecyclerViewAdapter);
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tag=getArguments().getString("TAG");
        phoneContactListFragmentViewModel phoneContactListFragmentViewModel = ViewModelProviders.of(this).get(phoneContactListFragmentViewModel.class);
        phoneContacts = phoneContactListFragmentViewModel.getAllContactsFromPhone();
        Log.e("phone contacts list = ", phoneContacts.toString());
        phoneContactsRecyclerViewAdapter = new PhoneContactsRecyclerViewAdapter(phoneContacts);
    }

    @OnClick(R.id.phoneContactsSelectBtn)
    void clickOnPhoneContact() {
        ArrayList<PhoneContact> selectedContacts=new ArrayList<>();
        for (int i = 0; i <= phoneContacts.size() - 1; i++) {
            if (phoneContacts.get(i).isSelected()) {
                if (!phoneContacts.get(i).getNumbers().equals(RayanApplication.getPref().getUsername()) )
                selectedContacts.add(phoneContacts.get(i));
            }
        }
        onAddUserToGroupSubmitClicked.addUserToGroupSubmitClicked(selectedContacts,tag);
        dismiss();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnAddUserToGroupSubmitClicked) {
            onAddUserToGroupSubmitClicked = (OnAddUserToGroupSubmitClicked) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }
    @Override
    public void onDetach() {
        super.onDetach();
        onAddUserToGroupSubmitClicked = null;
    }
    @OnTextChanged(R.id.phoneContactsSearchEditText)
    protected void onTextChanged(CharSequence text) {
        filter(text.toString());
    }
    void filter(String text){
        ArrayList<PhoneContact> temp = new ArrayList();
        for(PhoneContact d: phoneContacts){
            //or use .equal(text) with you want equal match
            //use .toLowerCase() for better matches
            if(d.getName().contains(text)){
                temp.add(d);
            }
        }
        //update recyclerview
        phoneContactsRecyclerViewAdapter.updateListBySearch(temp);
    }
}
