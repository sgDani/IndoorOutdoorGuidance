package com.indooratlas.android.sdk.examples.Adapter;

/**
 * Created by Daniel on 03/05/2016.
 */
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.indooratlas.android.sdk.examples.R;
import com.indooratlas.android.sdk.examples.imageview.ImageViewActivity;

public class ExampleViewHolder extends RecyclerView.ViewHolder {

    private final TextView tvText;
    private final ImageView image;

    public ExampleViewHolder(View itemView) {

        super(itemView);
        final String TAG = "CustomAdapter";

        tvText = (TextView) itemView.findViewById(R.id.tvText);
        image = (ImageView) itemView.findViewById(R.id.image);
        image.setImageResource(R.drawable.alko);
    }

    public void bind(ExampleModel model) {
        tvText.setText(model.getText());

    }
}