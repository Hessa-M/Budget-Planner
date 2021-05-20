package com.example.budget;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import com.example.budget.data.ItemContract.ItemEntry;
import java.io.ByteArrayOutputStream;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.DialogFragment;
import static android.app.Activity.RESULT_OK;

public class AddDialogFragment extends DialogFragment {

    private static final float PREFERRED_WIDTH = 250;
    private static final float PREFERRED_HEIGHT = 250;
    private static final int CAMERA_REQUEST = 1888;
    SwitchCompat switchCompat;
    View addView;


    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();

         addView = inflater.inflate(R.layout.dialog_add, null);

        switchCompat =  addView.findViewById(R.id.switchCompat);

        switchCompat.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v){
                if(switchCompat.isChecked())
                {
                    switchCompat.setText(getResources().getString(R.string.income));
                    switchCompat.setTextColor(getResources().getColor(R.color.IncomeDark));
                } else{
                    switchCompat.setText(getResources().getString(R.string.expenses));
                    switchCompat.setTextColor(getResources().getColor(R.color.ExpensesDark));
                }

            }
        });

         ImageButton ib =  addView.findViewById(R.id.itemPhoto );
         ib.setOnClickListener( new View.OnClickListener() {
             public void onClick(View v) {
                 takePhoto();
             }
         });


        final Dialog addDialog = builder.setView(addView)
                .setPositiveButton(R.string.add_item, null)
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        AddDialogFragment.this.getDialog().cancel();
                    }
                })
                .create();

        addDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {

                Button positiveButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);

                positiveButton.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        Boolean wantToCloseDialog = false;

                        EditText editTextDescription = addView.findViewById(R.id.descriptionEditText);
                        EditText editTextAmount = addView.findViewById(R.id.amountEditText);
                        SwitchCompat switchCompatType = addView.findViewById(R.id.switchCompat);
                        EditText editTextPhone = addView.findViewById(R.id.phoneEditText);
                        ImageButton itemImageButton = addView.findViewById(R.id.itemPhoto);

                        String description = editTextDescription.getText().toString().trim();
                        String amountString = editTextAmount.getText().toString().trim();
                        String switchCompatString = switchCompatType.getText().toString().trim();
                        String phone = editTextPhone.getText().toString().trim();
                        String itemPhoto = null;

                        if(itemImageButton != null) {
                            Bitmap image = ((BitmapDrawable) itemImageButton.getDrawable()).getBitmap();
                            itemPhoto = bitmapToString(resizeBitmap(image));
                        }
                        if (TextUtils.isEmpty(description)  || TextUtils.isEmpty(amountString)){
                            Toast.makeText(getActivity(), getString(R.string.item_info_not_empty), Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Float amount = Float.parseFloat(editTextAmount.getText().toString().trim());


                            insertItem(description, switchCompatString, amount, phone , itemPhoto);
                            wantToCloseDialog = true;
                        }

                        if(wantToCloseDialog)
                            addDialog.dismiss();
                    }
                });
            }
        });

        return addDialog;
    }

    public void takePhoto(){
        Intent cameraIntent = new Intent(android.provider.MediaStore. ACTION_IMAGE_CAPTURE );
        startActivityForResult(cameraIntent, CAMERA_REQUEST );
    }

    private static String bitmapToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        return Base64.encodeToString(b, Base64.DEFAULT);
    }

    public static Bitmap resizeBitmap(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float scaleWidth = PREFERRED_WIDTH / width;
        float scaleHeight = PREFERRED_HEIGHT / height;

        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bitmap, 0, 0, width, height, matrix, false);
        bitmap.recycle();
        return resizedBitmap;
    }

    public void onActivityResult( int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA_REQUEST) {
            if (resultCode == RESULT_OK) {
                Bitmap photo = (Bitmap) data.getExtras().get("data"); //6
                Bitmap scaledPhoto = Bitmap.createScaledBitmap(photo, (int)PREFERRED_WIDTH, (int)PREFERRED_HEIGHT, true); //7
                ImageButton imageItem = addView.findViewById(R.id.itemPhoto);
                imageItem.setImageBitmap(scaledPhoto);
            }
        }
    }

    private void insertItem(String description, String  type, Float amount, String phone, String image) {
        ContentValues values = new ContentValues();
        values.put(ItemEntry.COLUMN_DESCRIPTION, description);
        values.put(ItemEntry.COLUMN_AMOUNT, amount);
        values.put(ItemEntry.COLUMN_TYPE, type);
        values.put(ItemEntry.COLUMN_PHONE, phone);
        if (!"".equals(image))
            values.put(ItemEntry.COLUMN_PHOTO, image);
        getActivity().getContentResolver().insert(ItemEntry.CONTENT_URI, values);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PermissionUtils.MY_PERMISSIONS_READ_EXTERNAL_STORAGE: {

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), PermissionUtils.MY_PERMISSIONS_READ_EXTERNAL_STORAGE);
                } else {
                    Toast.makeText(getActivity(), getString(R.string.permission_denied), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
