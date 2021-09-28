package pws.monitoring.feri.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.gson.Gson;

import java.io.IOException;

import pws.monitoring.datalib.User;
import pws.monitoring.feri.ApplicationState;
import pws.monitoring.feri.R;
import pws.monitoring.feri.activities.NavigationActivity;
import pws.monitoring.feri.network.NetworkUtil;
import retrofit2.adapter.rxjava.HttpException;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class AccountFragment extends Fragment {
    EditText edtEmailUpdate;
    EditText edtNewPasswordUpdate;
    EditText edtIpDeviceUpdate;
    Button buttonUpdate;
    Button buttonLogout;

    private CompositeSubscription subscription;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        if (container != null) {
            container.removeAllViews();
        }

        final ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_account, container, false);

        bindGUI(rootView);
        bindValues();

        subscription = new CompositeSubscription();

        return rootView;
    }

    private void bindGUI(View v) {
        edtEmailUpdate = (EditText) v.findViewById(R.id.edtEmailUpdate);
        edtNewPasswordUpdate = (EditText) v.findViewById(R.id.edtNewPasswordUpdate);
        edtIpDeviceUpdate = (EditText) v.findViewById(R.id.edtIpDeviceUpdate);
        buttonUpdate = (Button) v.findViewById(R.id.buttonUpdate);
        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User user = new User();
                if (!edtEmailUpdate.getText().toString().equals(""))
                    user.setEmail(edtEmailUpdate.getText().toString());
                if (!edtNewPasswordUpdate.getText().toString().equals(""))
                    user.setPassword(edtNewPasswordUpdate.getText().toString());
                if (!edtIpDeviceUpdate.getText().toString().equals(""))
                    user.setIp(edtIpDeviceUpdate.getText().toString());
                user.setId(ApplicationState.loadLoggedUser().getId());
                Log.i("USER_UPDATE", user.toString());
                updateUser(user);
            }
        });
        buttonLogout = (Button) v.findViewById(R.id.buttonLogout);
        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutUser();
            }
        });
    }

    private void bindValues(){
        User user = ApplicationState.loadLoggedUser();
        edtEmailUpdate.setHint(user.getEmail());
        edtIpDeviceUpdate.setHint(user.getIp());
    }

    private void updateUser(User user) {
        Gson gson = ApplicationState.getGson();
        Log.i("USER_UPDATE", gson.toJson(user));
        subscription.add(NetworkUtil.getRetrofit().updateUser(user.getId(), user)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponseUpdate, this::handleError));
    }

    private void handleResponseUpdate(User user) {
        Log.i("USER_UPDATED", user.toString());
        ApplicationState.saveLoggedUser(user);
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.nav_host_fragment_activity_navigation, new AccountFragment()).commit();

    }

    private void handleError(Throwable error) {
        if (error instanceof HttpException) {
            try {

                String errorBody = ((HttpException) error).response().errorBody().string();
                Log.i("ERROR!", errorBody);
                //Response response = gson.fromJson(errorBody,Response.class);

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(requireContext(), error.getLocalizedMessage(),  Toast.LENGTH_LONG).show();
        }
    }

    private void logoutUser(){
        subscription.add(NetworkUtil.getRetrofit().logout()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponseLogout, this::handleError));
    }

    private void handleResponseLogout(Void v) {
        getActivity().finish();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        subscription.unsubscribe();
    }
}