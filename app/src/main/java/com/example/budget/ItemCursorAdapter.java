package com.example.budget;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.budget.data.ItemContract.ItemEntry;


public class ItemCursorAdapter extends CursorAdapter {

    private Context mContext;

    public ItemCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        mContext = context;

        TextView txtDescription =  view.findViewById(R.id.textViewDescription);
        ImageView typeImage=  view.findViewById(R.id.imageViewType);
        TextView txtAmount =  view.findViewById(R.id.textViewAmount);



        final String description = cursor.getString(cursor.getColumnIndexOrThrow(ItemEntry.COLUMN_DESCRIPTION));
        final Float amount = cursor.getFloat(cursor.getColumnIndexOrThrow(ItemEntry.COLUMN_AMOUNT));
        final String type = cursor.getString(cursor.getColumnIndexOrThrow(ItemEntry.COLUMN_TYPE));

        txtDescription.setText(description);
        txtAmount.setText(Float.toString(amount));

        if(type.equals("Income"))
        {
            typeImage.setImageResource(R.mipmap.baseline_add_white_48);
            typeImage.setBackgroundColor(context.getResources().getColor(R.color.IncomeDark));
        }
        else{
            typeImage.setImageResource(R.mipmap.sharp_minimize_white_48);
            typeImage.setBackgroundColor(context.getResources().getColor(R.color.ExpensesDark));
        }


    }
}