package pws.monitoring.feri.modals;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import pws.monitoring.feri.R;


public class ProgressModal extends DialogFragment {
    public static final String TAG = ProgressModal.class.getSimpleName();

    public AlertDialog.Builder builder;
    public LayoutInflater inflater;
    public View view;
    public Dialog dialog;

    public static ProgressModal newInstance() {
        ProgressModal modal = new ProgressModal();
        return modal;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        this.builder = this.builder != null ? this.builder : new AlertDialog.Builder(getActivity());
        this.inflater = this.inflater != null ? this.inflater : getActivity().getLayoutInflater();
        this.view = this.view != null ? this.view : inflater.inflate(R.layout.modal_spinning_progress, null);
        builder.setView(view);
        this.dialog = dialog != null ? this.dialog : builder.create();

        return dialog;
    }
}
