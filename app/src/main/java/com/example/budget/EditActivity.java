package com.example.budget;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.budget.data.ItemContract.ItemEntry;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NavUtils;

public class EditActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int EXISTING_ITEM_LOADER = 0;

    private Uri mCurrentPetUri;
    private static final int CAMERA_REQUEST = 1888;
    private static final float PREFERRED_WIDTH = 250;
    private static final float PREFERRED_HEIGHT = 250;

    private EditText mDescriptionEditText;
    private TextView mAmountEditText;
    private SwitchCompat mTypeSwitchCompat;
    private EditText mPhoneEditText;
    private ImageView mItemPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        Intent intent = getIntent();
        mCurrentPetUri = intent.getData();
        setTitle(getString(R.string.edit_item));
        getLoaderManager().initLoader(EXISTING_ITEM_LOADER, null, this);
        mDescriptionEditText = findViewById(R.id.descriptionEditText);
        mAmountEditText = findViewById(R.id.amountEditText);
        mTypeSwitchCompat = findViewById(R.id.switchCompat);
        mPhoneEditText = findViewById(R.id.phoneEditText);
        mItemPhoto = findViewById(R.id.itemPhoto);


        mTypeSwitchCompat.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v){
                if(mTypeSwitchCompat.isChecked())
                {
                    mTypeSwitchCompat.setText(getResources().getString(R.string.income));
                    mTypeSwitchCompat.setTextColor(getResources().getColor(R.color.IncomeDark));
                } else{
                    mTypeSwitchCompat.setText(getResources().getString(R.string.expenses));
                    mTypeSwitchCompat.setTextColor(getResources().getColor(R.color.ExpensesDark));
                }

            }
        });

        mItemPhoto.setOnClickListener( new View.OnClickListener() {
            public void onClick(View v) {
                takePhoto();
            }
        });
    }

    public void takePhoto(){
        Intent cameraIntent = new Intent(android.provider.MediaStore. ACTION_IMAGE_CAPTURE );
        startActivityForResult(cameraIntent, CAMERA_REQUEST );
    }


    private static Bitmap stringToBitmap(String encodedString) {
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
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

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                ItemEntry._ID,
                ItemEntry.COLUMN_DESCRIPTION,
                ItemEntry.COLUMN_AMOUNT,
                ItemEntry.COLUMN_TYPE,
                ItemEntry.COLUMN_PHOTO,
                ItemEntry.COLUMN_PHONE};
        return new CursorLoader(this, mCurrentPetUri, projection, null, null, null);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }
        if (cursor.moveToFirst()) {
            int descriptionColumnIndex = cursor.getColumnIndex(ItemEntry.COLUMN_DESCRIPTION);
            int amountColumnIndex = cursor.getColumnIndex(ItemEntry.COLUMN_AMOUNT);
            int typeColumnIndex = cursor.getColumnIndex(ItemEntry.COLUMN_TYPE);
            int phoneColumnIndex = cursor.getColumnIndex(ItemEntry.COLUMN_PHONE);
            int imageColumnIndex = cursor.getColumnIndex(ItemEntry.COLUMN_PHOTO);
            String name = cursor.getString(descriptionColumnIndex);
            Float amount = cursor.getFloat(amountColumnIndex);
            String type = cursor.getString(typeColumnIndex);
            String phone = cursor.getString(phoneColumnIndex);
            String itemPhoto = cursor.getString(imageColumnIndex);

            mDescriptionEditText.setText(name);
            mAmountEditText.setText(Float.toString(amount));
            if(type.equals("Income"))
            {
                mTypeSwitchCompat.setText(getResources().getString(R.string.income));
                mTypeSwitchCompat.setTextColor(getResources().getColor(R.color.IncomeDark));
                mTypeSwitchCompat.setChecked(true);
            }
            else
           {
                mTypeSwitchCompat.setText(getResources().getString(R.string.expenses));
                mTypeSwitchCompat.setTextColor(getResources().getColor(R.color.ExpensesDark));
                mTypeSwitchCompat.setChecked(false);
            }
            mPhoneEditText.setText(phone);

            mItemPhoto.setImageBitmap(stringToBitmap(itemPhoto));

        }

    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mDescriptionEditText.setText("");
        mAmountEditText.setText(Float.toString(0));
        mTypeSwitchCompat.setText("");
        mPhoneEditText.setText("");
        mItemPhoto.setImageDrawable(null);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(EditActivity.this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onActivityResult( int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA_REQUEST) {
            if (resultCode == RESULT_OK) {
                Bitmap photo = (Bitmap) data.getExtras().get("data"); //6
                Bitmap scaledPhoto = Bitmap.createScaledBitmap(photo, (int)PREFERRED_WIDTH, (int)PREFERRED_HEIGHT, true); //7
                ImageView imageItem = findViewById(R.id.itemPhoto);
                imageItem.setImageBitmap(scaledPhoto);
            }
        }
    }


    public void onOrder(View view) {
        String phone = mPhoneEditText.getText().toString().trim();
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + phone));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Can't call. change the permission in your phone setting.", Toast.LENGTH_SHORT).show();
            return;
        }
        startActivity(callIntent);
    }


    public void onSave(View view) {
        saveItem();
        finish();
    }

    private void saveItem() {
        boolean update1, update2, update3, update4, update5;
        int rowsAffected;
        ContentValues values = new ContentValues();

        // Description
        String description = mDescriptionEditText.getText().toString().trim();
        if (!description.equals("")) {
            values.put(ItemEntry.COLUMN_DESCRIPTION, description);
            update1 = true;
        }
        else {
            Toast.makeText(this, "Must enter description for Item.", Toast.LENGTH_SHORT).show();
            update1 = false;
        }

        // Amount
        Float amount = 0.0f;
        if (!"".equals(mAmountEditText.getText().toString().trim())) {
            amount = Float.parseFloat(mAmountEditText.getText().toString().trim());
            values.put(ItemEntry.COLUMN_AMOUNT, amount);
            update2 = true;
        }
        else{
            Toast.makeText(this, "Must enter amount for Item.", Toast.LENGTH_SHORT).show();
            update2 = false;
        }

        // Type
        String type = null;
        type = mTypeSwitchCompat.getText().toString().trim();
        values.put(ItemEntry.COLUMN_TYPE, type);
        update4 = true;

        // Image
        Bitmap image = null;
        String itemPhoto = null;
        if(mItemPhoto != null) {
            image = ((BitmapDrawable) mItemPhoto.getDrawable()).getBitmap();
            itemPhoto = bitmapToString(resizeBitmap(image));
        }

        if (!"".equals(itemPhoto) && !(null == itemPhoto)) {
            values.put(ItemEntry.COLUMN_PHOTO, itemPhoto);
            update5 = true;
        }else{
            Toast.makeText(this, "ERROR update Image.", Toast.LENGTH_SHORT).show();
            update5 = false;
        }
        //Phone Number
        String phone = mPhoneEditText.getText().toString().trim();
        if (!phone.equals("")) {
            values.put(ItemEntry.COLUMN_PHONE, phone);
            update3 = true;
        }
        else {
            Toast.makeText(this, "Must enter phone number.", Toast.LENGTH_SHORT).show();
            update3 = false;
        }

        if(update1 && update2 && update3 && update4 && update5) {
            rowsAffected = getContentResolver().update(mCurrentPetUri, values, null, null);
            if (rowsAffected == 0) {
                Toast.makeText(this, getString(R.string.update_item_failed), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.update_item_successful), Toast.LENGTH_SHORT).show();
            }
        }else
            Toast.makeText(this, getString(R.string.update_item_failed) + ". All fields must be filled.", Toast.LENGTH_SHORT).show();
    }


   public void onDelete(View view) {
        showDeleteConfirmationDialog();
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.delete_dialog_title));
        builder.setPositiveButton(getString(R.string.btn_delete), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteItem();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteItem() {
        int rowsDeleted = getContentResolver().delete(mCurrentPetUri, null, null);
        if (rowsDeleted == 0) {
            Toast.makeText(this, getString(R.string.delete_item_failed), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, getString(R.string.delete_item_successful), Toast.LENGTH_SHORT).show();
        }
        finish();
    }

    public boolean equals(Bitmap bitmap1, Bitmap bitmap2) {
        ByteBuffer buffer1 = ByteBuffer.allocate(bitmap1.getHeight() * bitmap1.getRowBytes());
        bitmap1.copyPixelsToBuffer(buffer1);

        ByteBuffer buffer2 = ByteBuffer.allocate(bitmap2.getHeight() * bitmap2.getRowBytes());
        bitmap2.copyPixelsToBuffer(buffer2);

        return Arrays.equals(buffer1.array(), buffer2.array());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PermissionUtils.MY_PERMISSIONS_READ_EXTERNAL_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startActivityForResult(new Intent(Intent.ACTION_PICK,
                                    android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI),
                            PermissionUtils.MY_PERMISSIONS_READ_EXTERNAL_STORAGE);
                } else {
                    Toast.makeText(EditActivity.this, getString(R.string.permission_denied), Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }
}
