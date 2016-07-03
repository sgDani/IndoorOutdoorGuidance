package com.indooratlas.android.sdk.examples.Adapter;

/**
 * Created by Daniel on 03/05/2016.
 */
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.indooratlas.android.sdk.examples.Activities.MainActivity;
import com.indooratlas.android.sdk.examples.Activities.MapsActivity;
import com.indooratlas.android.sdk.examples.Activities.menu_activity;
import com.indooratlas.android.sdk.examples.Fragments.MainFragment;
import com.indooratlas.android.sdk.examples.R;
import com.indooratlas.android.sdk.examples.imageview.ImageViewActivity;


import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created with Android Studio
 * User: Xaver
 * Date: 24/05/15
 */
public class ExampleAdapter extends RecyclerView.Adapter<ExampleAdapter.ExampleViewHolder> {
    private static final String TAG = "CustomAdapter";
    private final LayoutInflater mInflater;
    private final List<ExampleModel> mModels;
    public final static String EXTRA_MESSAGE = "com.mycompany.myfirstapp.MESSAGE";
    public static Integer pos=0;
    public static int position=0;


    public class ExampleViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;
        public ImageView image;

        public ExampleViewHolder(View v) {
            super(v);

            // Define click listener for the ViewHolder's View.

            textView = (TextView) v.findViewById(R.id.tvText);
            image = (ImageView) v.findViewById(R.id.image);



            //MainFragment.MOVIES.



            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "Element " + getPosition() + " clicked.");
                    hideKeyboardFrom(v.getContext(),v);
                    final String aux= textView.getText().toString();
                    final int pos = Arrays.asList(MainFragment.MOVIES).indexOf(aux);
                    position=pos;
                    if (MainActivity.SELLO) {
                        //Inside sello
                        Intent intent = new Intent(v.getContext(), ImageViewActivity.class);
                        v.getContext().startActivity(intent);
                    }
                    else{
                        // Outside sello
                        Intent intent = new Intent(v.getContext(), MapsActivity.class);
                        v.getContext().startActivity(intent);
                    }


                }
            });

        }

    }

    public static void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public ExampleAdapter(Context context, List<ExampleModel> models) {
        mInflater = LayoutInflater.from(context);
        mModels = new ArrayList<>(models);

    }

    @Override
    public ExampleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.item_example, parent, false);
        return new ExampleViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ExampleViewHolder holder, int position) {
        final ExampleModel model = mModels.get(position);
        int drawableId=0;
        String aux= model.getText();
        holder.textView.setText(aux);


        try {
            String text= aux;
            text= text.replace(" ","");
            text= text.replace("&","");
            text= text.replace("'","");
            text=text.replace("-","");
            text = text.replaceAll("[0-9]","");
            Class res = R.drawable.class;
            Field field = res.getField(text.toLowerCase());
            drawableId = field.getInt(null);
        }
        catch (Exception e) {
            Log.e("MyTag", "Failure to get drawable id.", e);
        }

        holder.image.setImageResource(drawableId);

    }



    @Override
    public int getItemCount() {
        return mModels.size();
    }

    public void animateTo(List<ExampleModel> models) {
        applyAndAnimateRemovals(models);
        applyAndAnimateAdditions(models);
        applyAndAnimateMovedItems(models);
    }

    private void applyAndAnimateRemovals(List<ExampleModel> newModels) {
        for (int i = mModels.size() - 1; i >= 0; i--) {
            final ExampleModel model = mModels.get(i);
            if (!newModels.contains(model)) {
                removeItem(i);
            }
        }
    }

    private void applyAndAnimateAdditions(List<ExampleModel> newModels) {
        for (int i = 0, count = newModels.size(); i < count; i++) {
            final ExampleModel model = newModels.get(i);
            if (!mModels.contains(model)) {
                addItem(i, model);
            }
        }
    }

    private void applyAndAnimateMovedItems(List<ExampleModel> newModels) {
        for (int toPosition = newModels.size() - 1; toPosition >= 0; toPosition--) {
            final ExampleModel model = newModels.get(toPosition);
            final int fromPosition = mModels.indexOf(model);
            if (fromPosition >= 0 && fromPosition != toPosition) {
                moveItem(fromPosition, toPosition);
            }
        }
    }

    public ExampleModel removeItem(int position) {
        final ExampleModel model = mModels.remove(position);
        notifyItemRemoved(position);
        return model;
    }

    public void addItem(int position, ExampleModel model) {
        mModels.add(position, model);
        notifyItemInserted(position);
    }

    public void moveItem(int fromPosition, int toPosition) {
        final ExampleModel model = mModels.remove(fromPosition);
        mModels.add(toPosition, model);
        notifyItemMoved(fromPosition, toPosition);
    }
}
