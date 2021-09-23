package pws.monitoring.feri.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.IOException;

import pws.monitoring.datalib.User;
import pws.monitoring.feri.ApplicationState;
import pws.monitoring.feri.R;
import pws.monitoring.feri.network.NetworkUtil;
import retrofit2.Response;
import retrofit2.adapter.rxjava.HttpException;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class RegisterActivity extends AppCompatActivity {

    ApplicationState applicationState;

    private Button buttonCancel;
    private Button buttonRegister;
    private EditText edtEmail;
    private EditText edtPasswd;
    private EditText edtReTypePasswd;

    private CompositeSubscription subscription;

    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        subscription = new CompositeSubscription();

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
    }

    public void onButtonCloseClick() {
        Intent data = new Intent();
        setResult(RESULT_CANCELED, data);
        finish();
    }

    public void onButtonRegisterClick() {
        if(!edtEmail.getText().toString().equals("") && !edtPasswd.getText().toString().equals("") &&
                !edtReTypePasswd.getText().toString().equals("")) {
            if (edtPasswd.getText().toString().length() < 8) {
                Toast.makeText(getBaseContext(), "Password is too short", Toast.LENGTH_LONG).show();
            } else {
                if (!edtPasswd.getText().toString().equals(edtReTypePasswd.getText().toString())) {
                    Toast.makeText(getBaseContext(), "Passwords don't match", Toast.LENGTH_LONG).show();
                } else {
                user.setEmail(edtEmail.getText().toString());
                user.setPassword(edtReTypePasswd.getText().toString());

                //API REQUEST
                registerProcess();
                }
            }
        } else {
            Toast.makeText(getBaseContext(), "All fields must be filled", Toast.LENGTH_LONG).show();
        }
    }

    private void registerProcess() {

        subscription.add(NetworkUtil.getRetrofit().register(user)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse, this::handleError));
    }

    private void handleResponse(User user) {
        Log.i("REGISTER", user.toString());
        Intent data = new Intent();
        setResult(RESULT_OK, data);
        finish();
    }

    private void handleError(Throwable error) {

        if (error instanceof HttpException) {
            try {
                String errorBody = ((HttpException) error).response().errorBody().string();
                Log.i("REGISTER ERROR", errorBody);

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(getBaseContext(), error.getMessage(),  Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        subscription.unsubscribe();
    }

}