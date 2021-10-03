package pws.monitoring.feri.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
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
import retrofit2.adapter.rxjava.HttpException;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class LogInActivity extends AppCompatActivity {
    public static final int REQUEST_CODE = 1;

    private Button buttonLogin;
    private Button buttonRegister;
    private EditText edtEmail;
    private EditText edtPassword;
    private User user;
    private CompositeSubscription subscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        subscription = new CompositeSubscription();

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
            Toast.makeText(getBaseContext(), "Registration complete", Toast.LENGTH_LONG).show();
        } else if (resultCode == RESULT_CANCELED) {
            Toast.makeText(getBaseContext(), "Registration cancelled", Toast.LENGTH_LONG).show();
        }
    }


    private void onClickLogin() {
        if(!edtEmail.getText().toString().equals("")
                && !edtPassword.getText().toString().equals("")) {
            user = new User();
            user.setEmail(edtEmail.getText().toString());
            user.setPassword(edtPassword.getText().toString());
            loginProcess();
        } else {
            Toast.makeText(getBaseContext(), "All fields are mandatory", Toast.LENGTH_LONG).show();
        }
    }

    private void loginProcess() {
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
        Log.i("LOGIN", user.toString());
        ApplicationState.saveLoggedUser(user);
        Intent intent = new Intent(this, NavigationActivity.class);
        startActivity(intent);
    }

    private void handleError(Throwable error) {
        if (error instanceof HttpException) {
            Gson gson = ApplicationState.getGson();
            try {

                String errorBody = ((HttpException) error).response().errorBody().string();
                Log.i("ERROR!", errorBody);

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(getBaseContext(), error.getLocalizedMessage(),  Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        subscription.unsubscribe();
    }
}