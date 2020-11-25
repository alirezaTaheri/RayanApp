package rayan.rayanapp.Listeners;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public interface BottomSheetFragmentClickListener {
    void onYesClicked(BottomSheetDialogFragment fragment);
    void onNoClicked(BottomSheetDialogFragment fragment);
}
