package pws.monitoring.feri.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;

import pws.monitoring.datalib.User;
import pws.monitoring.feri.ApplicationState;
import pws.monitoring.feri.R;
import pws.monitoring.feri.activities.LogInActivity;
import pws.monitoring.feri.activities.NavigationActivity;
import pws.monitoring.feri.events.OnFragmentChanged;
import pws.monitoring.feri.modals.ProgressModal;
import pws.monitoring.feri.network.NetworkError;
import pws.monitoring.feri.network.NetworkUtil;
import pws.monitoring.feri.services.UserUpdateService;
import retrofit2.adapter.rxjava.HttpException;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class AccountFragment extends Fragment {
    public static final String TAG =  AccountFragment.class.getSimpleName();

    private EditText edtEmailUpdate;
    private EditText edtNewPasswordUpdate;
    private Button buttonUpdate;
    private Button buttonLogout;
    private Button buttonDelete;
    private Switch switchNotifications;
    private ProgressModal progressModal;

    private CompositeSubscription subscription;

    private User user;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        EventBus.getDefault().post(new OnFragmentChanged(true));

        if (container != null) {
            container.removeAllViews();
        }

        final ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_account, container, false);

        user = ApplicationState.loadLoggedUser();
        subscription = new CompositeSubscription();

        progressModal = ProgressModal.newInstance();

        bindGUI(rootView);
        bindValues();

        return rootView;
    }

    private void bindGUI(View v) {
        edtEmailUpdate = (EditText) v.findViewById(R.id.edtEmailUpdate);
        edtNewPasswordUpdate = (EditText) v.findViewById(R.id.edtNewPasswordUpdate);

        progressModal.setCancelable(false);

        buttonUpdate = (Button) v.findViewById(R.id.buttonUpdate);
        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User user = new User();
                if (!TextUtils.isEmpty(edtEmailUpdate.getText().toString()) &&
                        android.util.Patterns.EMAIL_ADDRESS.matcher(edtEmailUpdate.getText().toString()).matches())
                    user.setEmail(edtEmailUpdate.getText().toString());

                if (!TextUtils.isEmpty(edtNewPasswordUpdate.getText().toString()) &&
                        edtNewPasswordUpdate.getText().toString().length() > 8)
                    user.setPassword(edtNewPasswordUpdate.getText().toString());

                user.setId(ApplicationState.loadLoggedUser().getId());

                Log.i(TAG, user.toString());
                updateUser(user);
            }
        });

        buttonLogout = (Button) v.findViewById(R.id.buttonLogout);
        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ApplicationState.runUpdateService = false;
                ApplicationState.removeSavedUser(user);
                logoutUser();
            }
        });

        buttonDelete = (Button) v.findViewById(R.id.buttonDeleteAccount);
        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
                builder.setMessage(requireContext().getResources().getString(R.string.dialog_delete_account))
                        .setPositiveButton(requireContext().getResources().getString(R.string.text_yes), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                ApplicationState.runUpdateService = false;
                                ApplicationState.removeSavedUser(user);
                                deleteAccount();
                            }
                        })
                        .setNegativeButton(requireContext().getResources().getString(R.string.text_no), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                builder.create().show();
            }
        });

        switchNotifications = (Switch) v.findViewById(R.id.switchNotifications);
        switchNotifications.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    ApplicationState.runUpdateService = true;
                    startService();
                } else {
                    ApplicationState.runUpdateService = false;
                }
            }
        });
    }

    private void bindValues(){
        User user = ApplicationState.loadLoggedUser();
        edtEmailUpdate.setHint(user.getEmail());
    }

    public void startService() {
        Intent serviceIntent = new Intent(requireContext(), UserUpdateService.class);
        ContextCompat.startForegroundService(requireContext(), serviceIntent);
    }

    private void updateUser(User user) {
        Log.v(TAG, ApplicationState.getGson().toJson(user));
        progressModal.show(getParentFragmentManager(), ProgressModal.TAG);
        subscription.add(NetworkUtil.getRetrofit().updateUser(user.getId(), user)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponseUpdate, this::handleError));
    }

    private void logoutUser(){
        progressModal.show(getParentFragmentManager(), ProgressModal.TAG);
        subscription.add(NetworkUtil.getRetrofit().logout()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponseLogout, this::handleError));
    }

    private void deleteAccount(){
        progressModal.show(getParentFragmentManager(), ProgressModal.TAG);
        subscription.add(NetworkUtil.getRetrofit().removeUser(user.getId())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponseDelete, this::handleError));
    }

    private void handleResponseUpdate(User user) {
        Log.v(TAG, user.toString());
        progressModal.dismiss();
        ApplicationState.saveLoggedUser(user);
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.nav_host_fragment_activity_navigation, new AccountFragment()).commit();

    }

    private void handleResponseDelete(Void v) {
        progressModal.dismiss();
        Toast.makeText(requireContext(), requireContext().getResources()
                        .getString(R.string.redirection_account_deleted), Toast.LENGTH_LONG).show();
        getActivity().finish();
    }

    private void handleResponseLogout(Void v) {
        progressModal.dismiss();
        getActivity().finish();
    }


    private void handleError(Throwable error) {
        Log.e(TAG, error.getMessage());
        progressModal.dismiss();
        NetworkError networkError = new NetworkError(error, getActivity());
        networkError.handleError();
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