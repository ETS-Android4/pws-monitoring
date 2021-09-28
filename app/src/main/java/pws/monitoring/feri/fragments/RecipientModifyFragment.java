package pws.monitoring.feri.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import pws.monitoring.datalib.User;
import pws.monitoring.feri.ApplicationState;
import pws.monitoring.feri.R;

public class RecipientModifyFragment extends Fragment {
    User user;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        if (container != null) {
            container.removeAllViews();
        }

        final ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_recipient_modify, container, false);

        bindGUI(rootView);
        bindValues();

        user = ApplicationState.loadLoggedUser();

        return rootView;
    }

    private void bindValues() {
    }

    private void bindGUI(ViewGroup v) {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
