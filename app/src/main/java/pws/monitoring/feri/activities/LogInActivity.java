package pws.monitoring.feri.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.SocketTimeoutException;

import pws.monitoring.datalib.User;
import pws.monitoring.feri.ApplicationState;
import pws.monitoring.feri.R;
import pws.monitoring.feri.config.ApplicationConfig;
import pws.monitoring.feri.modals.ProgressModal;
import pws.monitoring.feri.network.NetworkError;
import pws.monitoring.feri.network.NetworkUtil;
import retrofit2.adapter.rxjava.HttpException;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class LogInActivity extends AppCompatActivity {
    public static final String TAG =  LogInActivity.class.getSimpleName();
    public static final int REQUEST_CODE = 1;

    private Button buttonLogin;
    private Button buttonRegister;
    private EditText edtEmail;
    private EditText edtPassword;
    private ProgressModal progressModal;

    private User user;

    private CompositeSubscription subscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        subscription = new CompositeSubscription();
        user = ApplicationState.loadLoggedUser();

        progressModal = ProgressModal.newInstance();

        if (user != null){
            getUser(user.getId());
        }

        bindGUI();

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickRegister();
            }
        });

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickLogin();
            }
        });

    }

    public void bindGUI(){
        buttonLogin = (Button) findViewById(R.id.buttonLogIn);
        buttonRegister = (Button) findViewById(R.id.buttonGoRegister);
        edtEmail = (EditText) findViewById(R.id.edtEmail);
        edtPassword = (EditText) findViewById(R.id.edtPassword);
        progressModal.setCancelable(false);
    }

    public void onClickRegister() {
        this.startActivityForResult(
                new Intent(this.getBaseContext(), RegisterActivity.class),
                REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            Toast.makeText(getBaseContext(), getResources().getString(R.string.redirection_registration_complete), Toast.LENGTH_LONG).show();
        } else if (resultCode == RESULT_CANCELED) {
            Toast.makeText(getBaseContext(), getResources().getString(R.string.redirection_registration_cancelled), Toast.LENGTH_LONG).show();
        }
    }


    private void onClickLogin() {
        if(!TextUtils.isEmpty(edtEmail.getText().toString())
                && !TextUtils.isEmpty(edtPassword.getText().toString())) {
            user = new User();
            user.setEmail(edtEmail.getText().toString());
            user.setPassword(edtPassword.getText().toString());
            loginProcess();
        } else {
            Toast.makeText(getBaseContext(), getResources().getString(R.string.validation_fields_required), Toast.LENGTH_LONG).show();
        }
    }

    private void loginProcess() {
        progressModal.show(getSupportFragmentManager(), ProgressModal.TAG);
        subscription.add(NetworkUtil.getRetrofit().login(user)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponseLogIn, this::handleError));
    }

    private void getUser(String id){
        subscription.add(NetworkUtil.getRetrofit().getUser(id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponseUser, this::handleError));
    }

    private void handleResponseLogIn(User user) {
       getUser(user.getId());
    }

    private void handleResponseUser(User user) {
        Log.v(TAG, user.toString());
        progressModal.dismiss();
        ApplicationState.saveLoggedUser(user);
        Intent intent = new Intent(this, NavigationActivity.class);
        startActivity(intent);
    }

    private void handleError(Throwable error){
        Log.e(TAG, error.getMessage());
        progressModal.dismiss();
        NetworkError networkError = new NetworkError(error, LogInActivity.this);
        networkError.handleError();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        subscription.unsubscribe();
    }
}