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

import pws.monitoring.datalib.Plant;
import pws.monitoring.datalib.Recipient;
import pws.monitoring.datalib.User;
import pws.monitoring.feri.ApplicationState;
import pws.monitoring.feri.R;
import pws.monitoring.feri.events.OnUserUpdated;
import pws.monitoring.feri.network.NetworkUtil;
import retrofit2.adapter.rxjava.HttpException;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class RecipientModifyFragment extends Fragment {
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

        Bundle bundle = getArguments();
        recipient = ApplicationState.getGson().fromJson(bundle.getString("recipient"),
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
                String[] colors = {"Camera", "Gallery", "Cancel"};

                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                builder.setTitle("Pick an option");
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
                if(!edtMMoisture.getText().toString().equals(""))
                    recipient.getPlant().setMoisture(Integer.parseInt(edtMMoisture.getText().toString()));

                if(!edtMModifier.getText().toString().equals(""))
                    recipient.getPlant().setMoistureModifier(Integer.parseInt(edtMModifier.getText().toString()));

                if(!edtFrequency.getText().toString().equals(""))
                    recipient.getPlant().setFrequency(Integer.parseInt(edtFrequency.getText().toString()));

                if(!edtFModifier.getText().toString().equals(""))
                    recipient.getPlant().setFrequencyModifier(Integer.parseInt(edtFModifier.getText().toString()));

                handleApiRequest("updatePlant");
            }

        });

        buttonUpdateRecipient = (Button) v.findViewById(R.id.buttonUpdateRecipient);
        buttonUpdateRecipient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!edtByteAddress.getText().toString().equals(""))
                    recipient.setByteAddress(edtByteAddress.getText().toString());

                if(!edtRelayPin.getText().toString().equals(""))
                    recipient.setRelayPin(Integer.parseInt(edtRelayPin.getText().toString()));

                if(!edtMoisturePin.getText().toString().equals(""))
                    recipient.setMoisturePin(Integer.parseInt(edtMoisturePin.getText().toString()));

                if(scannedPlant != null)
                    recipient.setPlant(scannedPlant);

                if(!currentPhotoPath.equals(""))
                    recipient.setPath(currentPhotoPath);

                handleApiRequest("updateRecipient");
            }
        });

        buttonDeleteRecipient = (Button) v.findViewById(R.id.buttonDeleteRecipient);
        buttonDeleteRecipient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
                builder.setMessage("Are you sure you want to delete this recipient?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                handleApiRequest("deleteRecipient");
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
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
                Toast.makeText(getContext(), "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getContext(), "Scanned", Toast.LENGTH_LONG).show();
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
                Toast.makeText(requireContext(), "Cancelled", Toast.LENGTH_LONG).show();
            }
        }
    }

    //CREATE PHOTO
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        //File storageDir = getActivity().getBaseContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
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
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(getActivity().getBaseContext(),
                        "pws.monitoring.feri.fileprovider",
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
        switch (type){
            case "updatePlant":
                subscription.add(NetworkUtil.getRetrofit().updatePlant(user.getId(), recipient.getId(),
                        recipient.getPlant().getId(), recipient.getPlant())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe(this::handleResponse, this::handleError));
                break;
            case "updateRecipient":
                subscription.add(NetworkUtil.getRetrofit().updateRecipient(user.getId(), recipient.getId(), recipient)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe(this::handleResponse, this::handleError));
                break;
            case "deleteRecipient":
                subscription.add(NetworkUtil.getRetrofit().removeRecipient(user.getId(), recipient.getId())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe(this::handleResponse, this::handleError));
                break;
        }
    }

    private void handleResponse(User user) {
        ApplicationState.saveLoggedUser(user);
        EventBus.getDefault().post(new OnUserUpdated(user));
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        RecipientListFragment recipientListFragment = new RecipientListFragment();
        fragmentManager.beginTransaction().replace(R.id.nav_host_fragment_activity_navigation,
                recipientListFragment).commit();
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
            Log.i("REGISTER ERROR", error.getMessage());
        }
    }

    private void startQrScan(){
        IntentIntegrator integrator = IntentIntegrator.forSupportFragment(RecipientModifyFragment.this);

        integrator.setOrientationLocked(false);
        integrator.setPrompt("Scan QR code");
        integrator.setBeepEnabled(false);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);


        integrator.initiateScan();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
