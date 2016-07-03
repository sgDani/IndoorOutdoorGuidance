package com.indooratlas.android.sdk.examples.imageview;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;

import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.indooratlas.android.sdk.examples.Activities.MainActivity;
import com.indooratlas.android.sdk.examples.Activities.MapsActivity;
import com.indooratlas.android.sdk.examples.Activities.menu_activity;
import com.indooratlas.android.sdk.examples.R;

import java.util.Timer;
import java.util.TimerTask;

import static com.google.android.gms.internal.zzid.runOnUiThread;

/**
 * Extends great ImageView library by Dave Morrissey. See more:
 * https://github.com/davemorrissey/subsampling-scale-image-view.
 */
public class BlueDotView extends SubsamplingScaleImageView {

    private float radius = 1.0f;
    private float accuracy=2.0f;
    private PointF dotCenter = null;
    private Integer cont=0;

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public void setDotCenter(PointF dotCenter) {
        this.dotCenter = dotCenter;
    }

    public BlueDotView(Context context) {
        this(context, null);
    }

    public BlueDotView(Context context, AttributeSet attr) {
        super(context, attr);
        initialise();
    }

    public void setAccuracy (float accuracy){
        this.accuracy = accuracy;
    }


    private void initialise() {
        setWillNotDraw(false);
        setPanLimit(SubsamplingScaleImageView.PAN_LIMIT_CENTER);
    }

    public void setupTimer(){

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable(){
                    @Override
                    public void run(){
                        if (MainActivity.OUT && cont<1) {
                            cont++;
                            if(!MainActivity.IN2IN)
                                alertDialog();
                            else {
                                //Do something here.  IN2IN route and IN-OUT transition detected
                            }
                        }
                    }
                });
            }
        }, 0, 1000);

    }

    public void alertDialog(){
        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(getContext());
        alertDialogBuilder.setMessage("You are no longer in Sello!\nDo you want to continue with outdoor guidance?");

        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {

                Intent browserIntent = new Intent(getContext(), MapsActivity.class );
                browserIntent.putExtra("StringIneed", menu_activity.str_goal);
                getContext().startActivity(browserIntent);
                ImageViewActivity.finisher.finish();
            }
        });

        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        android.app.AlertDialog alertDialog1 = alertDialogBuilder.create();
        alertDialog1.show();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (!isReady()) {
            return;
        }

        if (dotCenter != null) {
            PointF vPoint = sourceToViewCoord(dotCenter);
            float scaledRadius = getScale() * radius;
            Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(getResources().getColor(R.color.ia_blue));
            canvas.drawCircle(vPoint.x, vPoint.y, scaledRadius, paint);
            paint.setAlpha(100);
            scaledRadius= 5*getScale()*accuracy;
            canvas.drawCircle(vPoint.x, vPoint.y,scaledRadius,paint);

            PointF myPoint = new PointF();
            for (int i=0;i<ImageViewActivity.path.size()-1;i++) {
                paint.setStrokeWidth(10);
                paint.setColor(Color.parseColor("#00ff00"));
                Integer node = ImageViewActivity.path.get(i);

                Double d = ImageViewActivity.nod_SELLO[node - 1][0];
                Float f = d.floatValue();
                Double d2 = ImageViewActivity.nod_SELLO[node - 1][1];
                Float f2 = d2.floatValue();
                myPoint.set(f, f2);
                PointF vmyPoint = sourceToViewCoord(myPoint);

                Integer node2 = ImageViewActivity.path.get(i + 1);

                Double d3 = ImageViewActivity.nod_SELLO[node2 - 1][0];
                Float f3 = d3.floatValue();
                Double d4 = ImageViewActivity.nod_SELLO[node2 - 1][1];
                Float f4 = d4.floatValue();
                myPoint.set(f3, f4);
                PointF vmyPoint2 = sourceToViewCoord(myPoint);
                canvas.drawLine(vmyPoint.x, vmyPoint.y,vmyPoint2.x,vmyPoint2.y, paint);

                //Paint the line between the goal and the last node
                if (i==0){
                    PointF vgoal=sourceToViewCoord(ImageViewActivity.goal);
                    canvas.drawLine(vgoal.x, vgoal.y,vmyPoint.x, vmyPoint.y,paint);
                }
            }
        }
    }
}
