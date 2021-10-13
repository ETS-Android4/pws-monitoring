package pws.monitoring.feri.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;

import pws.monitoring.datalib.User;
import pws.monitoring.feri.ApplicationState;
import pws.monitoring.feri.R;
import pws.monitoring.feri.modals.AddRecipientModal;
import pws.monitoring.feri.modals.ProgressModal;
import pws.monitoring.feri.network.NetworkError;
import pws.monitoring.feri.network.NetworkUtil;
import retrofit2.Response;
import retrofit2.adapter.rxjava.HttpException;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class RegisterActivity extends AppCompatActivity {
    public static final String TAG =  RegisterActivity.class.getSimpleName();

    private Button buttonCancel;
    private Button buttonRegister;
    private EditText edtEmail;
    private EditText edtPasswd;
    private EditText edtReTypePasswd;
    private ProgressModal progressModal;

    private CompositeSubscription subscription;

    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        subscription = new CompositeSubscription();
        user = new User();
        progressModal = ProgressModal.newInstance();

        bindGUI();

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonCloseClick();
            }
        });

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonRegisterClick();
            }
        });
    }

    public void bindGUI() {
        buttonCancel = (Button) findViewById(R.id.buttonCancel);
        buttonRegister = (Button) findViewById(R.id.buttonRegister);
        edtEmail = (EditText) findViewById(R.id.edtEmailRegister);
        edtPasswd = (EditText) findViewById(R.id.edtPasswdRegister);
        edtReTypePasswd = (EditText) findViewById(R.id.edtRePasswdRegister);
        progressModal.setCancelable(false);
    }

    public void onButtonCloseClick() {
        Intent data = new Intent();
        setResult(RESULT_CANCELED, data);
        finish();
    }

    public void onButtonRegisterClick() {
        if(validateRegistration(edtEmail.getText().toString(), edtPasswd.getText().toString(),
           edtReTypePasswd.getText().toString())) {
            user.setEmail(edtEmail.getText().toString());
            user.setPassword(edtReTypePasswd.getText().toString());
            registerProcess();
        } else {
            Toast.makeText(getBaseContext(), getResources().getString(R.string.validation_fields_required), Toast.LENGTH_LONG).show();
        }
    }

    private boolean validateRegistration(String email, String p1, String p2){
        if(TextUtils.isEmpty(email) || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            edtEmail.setError(getResources().getString(R.string.validation_email_format));
            return false;
        }

        if(p1.length() < 8){
            edtPasswd.setError(getResources().getString(R.string.validation_password_length));
            return false;
        }

        if(!p1.equals(p2)){
            edtReTypePasswd.setError(getResources().getString(R.string.validation_passwords_match));
            return false;
        }

        return true;
    }

    private void registerProcess() {
        progressModal.show(getSupportFragmentManager(), ProgressModal.TAG);
        subscription.add(NetworkUtil.getRetrofit().register(user)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse, this::handleError));
    }

    private void handleResponse(User user) {
        Log.i(TAG, user.toString());
        progressModal.dismiss();
        Intent data = new Intent();
        setResult(RESULT_OK, data);
        finish();
    }

    private void handleError(Throwable error) {
        progressModal.dismiss();
        NetworkError networkError = new NetworkError(error, RegisterActivity.this);
        networkError.handleError();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        subscription.unsubscribe();
    }

}