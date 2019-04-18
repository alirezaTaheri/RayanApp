package rayan.rayanapp.Listeners;

import java.util.ArrayList;

import rayan.rayanapp.Data.PhoneContact;

public interface OnAddUserToGroupSubmitClicked {
    void addUserToGroupSubmitClicked(ArrayList<PhoneContact> SelectedContacts, String Tag);
}
