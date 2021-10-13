package pws.monitoring.feri.network;

import android.content.Context;
import android.content.DialogInterface;

import androidx.appcompat.app.AlertDialog;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.SocketTimeoutException;

import pws.monitoring.feri.R;
import pws.monitoring.feri.config.ApplicationConfig;
import retrofit2.adapter.rxjava.HttpException;

public class NetworkError {
    Throwable error;
    Context context;

    public NetworkError(Throwable error, Context context) {
        this.error = error;
        this.context = context;
    }

    public void handleError() {
        if (error instanceof HttpException) {
            try {
                int statusCode = ((HttpException) error).code();
                String errorBody = ((HttpException) error).response().errorBody().string();
                Document document = Jsoup.parse(errorBody);
                Elements elements = document.select("h1");
                displayDialog(statusCode, elements.text());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (error instanceof SocketTimeoutException) {
            displayDialog(ApplicationConfig.ERROR_CODE, context.getResources().getString(R.string.error_socket_timeout));
        } else {
            displayDialog(ApplicationConfig.ERROR_CODE, context.getResources().getString(R.string.error_general));
        }
    }

    private void displayDialog(int statusCode, String errorMessage){
        String title = "";
        if(statusCode == -1){
            title = context.getResources().getString(R.string.text_error);
        } else {

            switch (statusCode){
                case 401:
                    title = context.getResources().getString(R.string.text_401);
                    break;
                case 403:
                    title = context.getResources().getString(R.string.text_403);
                    break;
                case 404:
                    title = context.getResources().getString(R.string.text_404);
                    break;
                case 500:
                    title = context.getResources().getString(R.string.text_500);
                    break;
            }

        }
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title).setMessage(errorMessage)
                .setPositiveButton(context.getResources().getString(R.string.text_ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        builder.create().show();
    }
}
