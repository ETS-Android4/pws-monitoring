package pws.monitoring.feri.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.greenrobot.eventbus.EventBus;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

import pws.monitoring.datalib.Plant;
import pws.monitoring.datalib.Recipient;
import pws.monitoring.datalib.User;
import pws.monitoring.feri.ApplicationState;
import pws.monitoring.feri.R;
import pws.monitoring.feri.activities.LogInActivity;
import pws.monitoring.feri.config.ApplicationConfig;
import pws.monitoring.feri.events.OnUserUpdated;
import pws.monitoring.feri.modals.ProgressModal;
import pws.monitoring.feri.network.NetworkError;
import pws.monitoring.feri.network.NetworkUtil;
import retrofit2.adapter.rxjava.HttpException;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class RecipientModifyFragment extends Fragment {
    public static final String TAG =  RecipientModifyFragment.class.getSimpleName();

    private static final int REQUEST_CAMERA = 1;
    private static final int REQUEST_CAMERA_OPEN = 2;
    private static final int REQUEST_GALLERY = 3;

    private String currentPhotoPath;

    private EditText edtMMoisture;
    private EditText edtMModifier;
    private EditText edtFrequency;
    private EditText edtFModifier;
    private EditText edtByteAddress;
    private EditText edtRelayPin;
    private EditText edtMoisturePin;
    private Button buttonChangePlant;
    private Button buttonUpdatePicture;
    private Button buttonUpdatePlant;
    private Button buttonUpdateRecipient;
    private Button buttonDeleteRecipient;
    private ProgressModal progressModal;

    private User user;
    private Recipient recipient;
    private Plant scannedPlant;

    private CompositeSubscription subscription;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        if (container != null) {
            container.removeAllViews();
        }

        final ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_recipient_modify, container, false);

        user = ApplicationState.loadLoggedUser();
        subscription = new CompositeSubscription();

        progressModal = ProgressModal.newInstance();

        Bundle bundle = getArguments();
        recipient = ApplicationState.getGson().fromJson(bundle.getString(ApplicationConfig.RECIPIENT_KEY),
                Recipient.class);

        bindGUI(rootView);
        bindValues();

        return rootView;
    }

    private void bindValues() {
        edtMMoisture.setHint(String.valueOf(recipient.getPlant().getMoisture()));
        edtMModifier.setHint(String.valueOf(recipient.getPlant().getMoistureModifier()));
        edtFrequency.setHint(String.valueOf(recipient.getPlant().getFrequency()));
        edtFModifier.setHint(String.valueOf(recipient.getPlant().getFrequencyModifier()));
        edtByteAddress.setHint(recipient.getByteAddress());
        edtRelayPin.setHint(String.valueOf(recipient.getRelayPin()));
    }

    private void bindGUI(ViewGroup v) {
        edtMMoisture = (EditText) v.findViewById(R.id.edtMMoisture);
        edtMModifier = (EditText) v.findViewById(R.id.edtMModifier);
        edtFrequency = (EditText) v.findViewById(R.id.edtFrequency);
        edtFModifier = (EditText) v.findViewById(R.id.edtFModifier);
        edtByteAddress = (EditText) v.findViewById(R.id.edtByteAddress);
        edtRelayPin = (EditText) v.findViewById(R.id.edtRelayPin);
        edtMoisturePin = (EditText) v.findViewById(R.id.edtMoisturePin);

        progressModal.setCancelable(false);

        buttonChangePlant = (Button) v.findViewById(R.id.buttonChangePlant);
        buttonChangePlant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startQrScan();
            }
        });

        buttonUpdatePicture = (Button) v.findViewById(R.id.buttonUpdatePicture);
        buttonUpdatePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] colors = {
                        getActivity().getResources().getString(R.string.text_camera),
                        getActivity().getResources().getString(R.string.text_gallery),
                        getActivity().getResources().getString(R.string.text_cancel),
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                builder.setTitle(getActivity().getResources().getString(R.string.title_option));
                builder.setItems(colors, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case 0:
                                pickCamera();
                                break;
                            case 1:
                                pickGallery();
                                break;
                            case 2:
                                dialog.dismiss();
                                break;
                        }
                    }
                });
                builder.show();
            }
        });

        buttonUpdatePlant = (Button) v.findViewById(R.id.buttonUpdatePlant);
        buttonUpdatePlant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!TextUtils.isEmpty(edtMMoisture.getText().toString()))
                    recipient.getPlant().setMoisture(Integer.parseInt(edtMMoisture.getText().toString()));

                if(!TextUtils.isEmpty(edtMModifier.getText().toString()))
                    recipient.getPlant().setMoistureModifier(Integer.parseInt(edtMModifier.getText().toString()));

                if(!TextUtils.isEmpty(edtFrequency.getText().toString()))
                    recipient.getPlant().setFrequency(Integer.parseInt(edtFrequency.getText().toString()));

                if(!TextUtils.isEmpty(edtFModifier.getText().toString()))
                    recipient.getPlant().setFrequencyModifier(Integer.parseInt(edtFModifier.getText().toString()));

                handleApiRequest(ApplicationConfig.API_UPDATE_P_KEY);
            }

        });

        buttonUpdateRecipient = (Button) v.findViewById(R.id.buttonUpdateRecipient);
        buttonUpdateRecipient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!TextUtils.isEmpty(edtByteAddress.getText().toString()) &&
                        Pattern.matches("[0-1]{4}", edtByteAddress.getText().toString()))
                    recipient.setByteAddress(edtByteAddress.getText().toString());

                if(!TextUtils.isEmpty(edtRelayPin.getText().toString()) &&
                        (Integer.parseInt(edtRelayPin.getText().toString()) > 20 &&
                                Integer.parseInt(edtRelayPin.getText().toString()) < 54))
                    recipient.setRelayPin(Integer.parseInt(edtRelayPin.getText().toString()));

                if(!TextUtils.isEmpty(edtMoisturePin.getText().toString()))
                    recipient.setMoisturePin(Integer.parseInt(edtMoisturePin.getText().toString()));

                if(scannedPlant != null)
                    recipient.setPlant(scannedPlant);

                if(!TextUtils.isEmpty(currentPhotoPath))
                    recipient.setPath(currentPhotoPath);

                handleApiRequest(ApplicationConfig.API_UPDATE_R_KEY);
            }
        });

        buttonDeleteRecipient = (Button) v.findViewById(R.id.buttonDeleteRecipient);
        buttonDeleteRecipient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
                builder.setMessage(getResources().getString(R.string.dialog_delete_recipient))
                        .setPositiveButton(getResources().getString(R.string.text_yes), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                handleApiRequest(ApplicationConfig.API_DELETE_KEY);
                            }
                        })
                        .setNegativeButton(getResources().getString(R.string.text_no), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                builder.create().show();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

         if (requestCode == REQUEST_GALLERY) {
            if (resultCode == Activity.RESULT_OK) {
                Uri contentUri = data.getData();
                currentPhotoPath = getPathFromURI(contentUri);
            }
        }

        if(result != null) {
            if(result.getContents() == null) {
                Toast.makeText(getContext(), getResources().getString(R.string.text_cancelled), Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getContext(), getResources().getString(R.string.text_scanned), Toast.LENGTH_LONG).show();
                scannedPlant = ApplicationState.getGson().fromJson(result.getContents(), Plant.class);
            }
        }
    }

    private void pickCamera() {
        askForPermission();
    }

    private void pickGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_GALLERY);
    }

    //PERMISSION
    private void askForPermission() {
        if(ContextCompat.checkSelfPermission(getActivity().getBaseContext(), Manifest.permission.CAMERA) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[] {Manifest.permission.CAMERA}, REQUEST_CAMERA);
        } else {
            dispatchTakePictureIntent();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CAMERA) {
            if (grantResults.length < 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                dispatchTakePictureIntent();
            } else {
                Toast.makeText(requireContext(), getResources().getString(R.string.text_cancelled), Toast.LENGTH_LONG).show();
            }
        }
    }

    //CREATE PHOTO
    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat(ApplicationConfig.DATE_TIME_FORMAT_PICTURE).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    //OPEN CAMERA
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getActivity().getBaseContext().getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }

            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(getActivity().getBaseContext(),
                        ApplicationConfig.AUTHORITY_KEY,
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_CAMERA_OPEN);
            }
        }
    }

    //GET PATH
    public String getPathFromURI(Uri contentUri) {
        String res = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getActivity().getContentResolver().query(contentUri, proj, null, null, null);
        if (((Cursor) cursor).moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();
        return res;
    }

    private void handleApiRequest(String type){
        progressModal.show(getParentFragmentManager(), ProgressModal.TAG);
        switch (type){
            case ApplicationConfig.API_UPDATE_P_KEY:
                subscription.add(NetworkUtil.getRetrofit().updatePlant(user.getId(), recipient.getId(),
                        recipient.getPlant().getId(), recipient.getPlant())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe(this::handleResponse, this::handleError));
                break;
            case ApplicationConfig.API_UPDATE_R_KEY:
                subscription.add(NetworkUtil.getRetrofit().updateRecipient(user.getId(), recipient.getId(), recipient)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe(this::handleResponse, this::handleError));
                break;
            case ApplicationConfig.API_DELETE_KEY:
                subscription.add(NetworkUtil.getRetrofit().removeRecipient(user.getId(), recipient.getId())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe(this::handleResponse, this::handleError));
                break;
        }
    }

    private void handleResponse(User user) {
        progressModal.dismiss();
        ApplicationState.saveLoggedUser(user);
        EventBus.getDefault().post(new OnUserUpdated(user));
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        RecipientListFragment recipientListFragment = new RecipientListFragment();
        fragmentManager.beginTransaction().replace(R.id.nav_host_fragment_activity_navigation,
                recipientListFragment).commit();
    }

    private void handleError(Throwable error) {
        progressModal.dismiss();
        Log.e(TAG, error.getMessage());
        NetworkError networkError = new NetworkError(error, getActivity());
        networkError.handleError();
    }

    private void startQrScan(){
        IntentIntegrator integrator = IntentIntegrator.forSupportFragment(RecipientModifyFragment.this);

        integrator.setOrientationLocked(false);
        integrator.setPrompt(getResources().getString(R.string.text_scan));
        integrator.setBeepEnabled(false);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);

        integrator.initiateScan();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
