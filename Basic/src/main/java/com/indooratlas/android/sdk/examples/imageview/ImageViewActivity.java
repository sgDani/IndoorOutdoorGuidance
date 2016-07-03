package com.indooratlas.android.sdk.examples.imageview;

import android.app.Activity;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.PointF;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.indooratlas.android.sdk.IALocation;
import com.indooratlas.android.sdk.IALocationListener;
import com.indooratlas.android.sdk.IALocationManager;
import com.indooratlas.android.sdk.IALocationRequest;
import com.indooratlas.android.sdk.IARegion;
import com.indooratlas.android.sdk.examples.Adapter.ExampleAdapter;
import com.indooratlas.android.sdk.examples.R;
import com.indooratlas.android.sdk.examples.SdkExample;
import com.indooratlas.android.sdk.resources.IAFloorPlan;
import com.indooratlas.android.sdk.resources.IALatLng;
import com.indooratlas.android.sdk.resources.IALocationListenerSupport;
import com.indooratlas.android.sdk.resources.IAResourceManager;
import com.indooratlas.android.sdk.resources.IAResult;
import com.indooratlas.android.sdk.resources.IAResultCallback;
import com.indooratlas.android.sdk.resources.IATask;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

@SdkExample(description = R.string.example_imageview_description)
public class ImageViewActivity extends FragmentActivity {

    private static final String TAG = "IndoorAtlasExample";

    // blue dot radius in meters
    private static final float dotRadius = 1.0f;

    private IALocationManager mIALocationManager;
    private IAResourceManager mFloorPlanManager;
    private IATask<IAFloorPlan> mPendingAsyncResult;
    private IAFloorPlan mFloorPlan;
    private BlueDotView mImageView;
    private long mDownloadId;
    private DownloadManager mDownloadManager;


    public static PointF goal = new PointF();
    public Integer[][] dbn_SELLO = new Integer[64][64];
    public static Double [][] nod_SELLO = new Double[64][2];
    public static Double [][] nod_aux_SELLO=new Double[64][2];
    public Integer [][] parent_SELLO = new Integer[64][3];
    public static ArrayList<Integer> path = new ArrayList<Integer>();

    public static Integer xScaleS = 2430;
    public static Integer yScaleS = 2120;

    public static Integer xLengthS = 120;
    public static Integer yLengthS =100;

    public static Activity finisher;

    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);
        // prevent the screen going to sleep while app is on foreground
        findViewById(android.R.id.content).setKeepScreenOn(true);

        //Hide keyboard
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

        loadingDialog();

        finisher=this;

        mImageView = (BlueDotView) findViewById(R.id.imageView);
        mImageView.setupTimer();
        mDownloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        mIALocationManager = IALocationManager.create(this);
        mFloorPlanManager = IAResourceManager.create(this);

        /* optional setup of floor plan id
           if setLocation is not called, then location manager tries to find
           location automatically */
        final String floorPlanId = getString(R.string.indooratlas_floor_plan_id);
        if (!TextUtils.isEmpty(floorPlanId)) {
            final IALocation location = IALocation.from(IARegion.floorPlan(floorPlanId));
            mIALocationManager.setLocation(location);
        }
        goal_set(ExampleAdapter.position);
        readFiles_Sello();
    }

    private void loadingDialog() {
        dialog = new ProgressDialog(this); // this = YourActivity
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage("Keep walking to get your actual position...");
        dialog.setIndeterminate(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    private IALocationListener mLocationListener = new IALocationListenerSupport() {
        @Override
        public void onLocationChanged(IALocation location) {
            Log.d(TAG, "location is: " + location.getLatitude() + "," + location.getLongitude());
            if (mImageView != null && mImageView.isReady()) {
                IALatLng latLng = new IALatLng(location.getLatitude(), location.getLongitude());
                PointF point = mFloorPlan.coordinateToPoint(latLng);
                mImageView.setDotCenter(point);
                mImageView.postInvalidate();
                mImageView.setAccuracy(location.getAccuracy());
                if (!pointIsInPath(point)){
                    if (location.getAccuracy()<5){
                        dialog.cancel();
                        Astar_SELLO(goal, point);
                    }
                }

            }
        }
    };

    private IARegion.Listener mRegionListener = new IARegion.Listener() {

        @Override
        public void onEnterRegion(IARegion region) {
            if (region.getType() == IARegion.TYPE_FLOOR_PLAN) {
                String id = region.getId();
                Log.d(TAG, "floorPlan changed to " + id);
                fetchFloorPlan(id);
            }
        }

        @Override
        public void onExitRegion(IARegion region) {
            // leaving a previously entered region
        }

    };

    private void goal_set(int position) {
        switch (position) {
            //3Amigos
            case 0: goal.set(2050,280);
                break;

            //Aleksi
            case 1: goal.set(1820,1130);
                break;

            // "Arnold",
            case 2: goal.set(280,1260);
                break;

            // "Belizia",
            case 3: goal.set(1130,1130);
                break;

            // "BikBok",
            case 4: goal.set(520,1170);
                break;

            // "BR lelut",
            case 5: goal.set(890,1550);
                break;

            // "BURGER KING",
            case 6: goal.set(270,1480);
                break;

            // "Caffi",
            case 7: goal.set(1710,920);
                break;

            // "Cubus",
            case 8: goal.set(910,830);
                break;

            // "DNA",
            case 9: goal.set(1900,680);
                break;

            // "Dressmann",
            case 10: goal.set(1460,830);
                break;

            // "Du Pareil",
            case 11: goal.set(1000,1300);
                break;

            // "Elisa",
            case 12: goal.set(1930,620);
                break;

            // "Emotion",
            case 13: goal.set(1015,830);
                break;

            // "Expreso House",
            case 14: goal.set(410,1300);
                break;

            // "Expert",
            case 15: goal.set(1850,330);
                break;

            // "Feel Vegas",
            case 16: goal.set(410,860);
                break;

            // "Fiilinki",
            case 17: goal.set(1000,1400);
                break;

            // "Finlayson",
            case 18: goal.set(1220,1130);
                break;

            // "Fonum",
            case 19: goal.set(770,1130);
                break;

            // "GameStop",
            case 20: goal.set(1780,650);
                break;

            // "Gelato",
            case 21: goal.set(1700,800);
                break;

            // "Gina Tricot",
            case 22: goal.set(1670,1130);
                break;

            // "H&M",
            case 23: goal.set(1540,1130);
                break;

            // "Halonen",
            case 24: goal.set(580,830);
                break;

            // "Hanko Sushi",
            case 25: goal.set(340,1200);
                break;

            // "Hesburger",
            case 26: goal.set(620,980);
                break;

            // "Iittala",
            case 27: goal.set(820,830);
                break;

            // "Indiska",
            case 28: goal.set(340,1400);
                break;

            // "Instrumentarium",
            case 29: goal.set(1820,840);
                break;

            // "Jack & Jones",
            case 30: goal.set(1420,1130);
                break;

            // "Jesper Junior",
            case 31: goal.set(890,1400);
                break;

            // "Jungle Juice Bar",
            case 32: goal.set(1820,740);
                break;

            // "Kaivokukka",
            case 33: goal.set(1600,800);
                break;

            // "Karkkitori",
            case 34: goal.set(1990,460);
                break;

            // "Kukkakauppa Sitomo",
            case 35: goal.set(220,1320);
                break;

            // "Kung Food Panda",
            case 36: goal.set(1960,540);
                break;

            // "Life",
            case 37: goal.set(1820,1000);
                break;

            // "Makuuni",
            case 38: goal.set(1000,1720);
                break;

            // "Mango",
            case 39: goal.set(1390,830);
                break;

            // "Martins",
            case 40: goal.set(1210,830);
                break;

            // "Ninja",
            case 41: goal.set(1820,920);
                break;

            // "Nissen",
            case 42: goal.set(1900,760);
                break;

            // "OLearys",
            case 43: goal.set(460,830);
                break;

            // "Oliva",
            case 44: goal.set(860,1130);
                break;

            // "Pentik",
            case 45: goal.set(410,1130);
                break;

            // "Punainen Rusetti",
            case 46: goal.set(1050,1130);
                break;

            // "R-Kioski",
            case 47: goal.set(410,980);
                break;

            // "Rajala",
            case 48: goal.set(1730,730);
                break;

            // "Ravintola Base",
            case 49: goal.set(1000,1630);
                break;

            // "Robert's Coffee",
            case 50: goal.set(1580,980);
                break;

            // "Sonera",
            case 51: goal.set(1850,460);
                break;

            // "Subway",
            case 52: goal.set(2020,390);
                break;

            // "Teknikmagasinet",
            case 53: goal.set(1820,530);
                break;

            // "The Body Shop",
            case 54: goal.set(410,1050);
                break;

            // "Zip",
            case 55: goal.set(700,830);
                break;

            default:goal.set(70,1450);
        }
    }

    public void Astar_SELLO (PointF goal, PointF origin ) {
        path.clear();

        Integer node_start=findClosestNode_Sello(origin);
        Integer node_goal=findClosestNode_Sello(goal);
        Integer node_current=1;
        //Integer[] node_successor= new Integer[4];

        Integer successor_current_cost=0;

        Integer[] f = new Integer[64];
        Integer[] g = new Integer[64];
        Integer[] h = new Integer[64];
        Integer[] father = new Integer[64];


        for (int i=0; i<64;i++){
            g[i]=0;
            h[i]=0;
            f[i]=0;
            father[i]=99;
        }

        ArrayList<Integer> open = new ArrayList<Integer>();
        ArrayList<Integer> closed = new ArrayList<Integer>();

        open.add(node_start);

        while (!(open.isEmpty())){

            // Get the node from the open list with the lowest f
            for (int i=0; i<open.size();i++){
                if (i==0)
                    node_current=open.get(i);
                f[open.get(i)]=g[open.get(i)]+h[open.get(i)];

                if (f[open.get(i)]<f[node_current])
                    node_current=open.get(i);
            }

            if (node_current==node_goal){
                break;
            }

            Integer[] node_successor=parent_SELLO[node_current-1];
            Integer successor;

            for(int i=0;i<node_successor.length;i++){
                if (node_successor[i]>0)
                    successor=node_successor[i];
                else
                    continue;
                successor_current_cost=g[node_current]+dbn_SELLO[node_current-1][successor-1];
                if (open.contains(successor)) {
                    if (g[successor] < successor_current_cost) {
                        continue;
                    }
                    else {
                        //Do nothing
                    }
                }
                else {
                    if (closed.contains(successor)) {
                        if (g[successor] < successor_current_cost) {
                            continue;
                        }
                        closed.remove(successor);
                        open.add(successor);
                    } else {
                        open.add(successor);
                        h[successor] = distance(successor, node_goal);
                    }
                }
                g[successor]=successor_current_cost;
                father[successor]=node_current;
                f[successor]=g[successor]+h[successor];
            }
            open.remove(node_current);
            closed.add(node_current);
        }
        Integer next = node_current;
        // get the path
        while (!(next.equals(node_start))){
            path.add(next);
            next=father[next];
        }
        path.add(node_start);

        next=5;

    }

    private Integer findClosestNode_Sello(PointF origin) {
        Integer node=1;
        Double dist;
        Double min=500.0;
        for (int i=0;i<nod_SELLO.length-1;i++){
            dist=Math.sqrt((nod_SELLO[i][0]-origin.x)*(nod_SELLO[i][0]-origin.x)+(nod_SELLO[i][1]-origin.y)*(nod_SELLO[i][1]-origin.y));
            if (i==0)
                min=dist;
            else
            if(min>dist) {
                min = dist;
                node=i+1;
            }
        }

        return node;
    }

    public void readFiles_Sello () {

        int i = 0;
        int j = 0;
        String line = "";
        String aux = "";
        int myint = 0;
        double mydouble = 0;

        //////////////////////////
        // Read dbn_SELLO
        /////////////////////////
        try {

            File myFile = new File("/sdcard/dbn.txt");
            i = 0;
            FileInputStream fIn = new FileInputStream(myFile);
            BufferedReader myReader = new BufferedReader(
                    new InputStreamReader(fIn));
            String aDataRow = "";
            String aBuffer = "";
            while ((aDataRow = myReader.readLine()) != null) {
                aBuffer += aDataRow + "\n";
                String[] tokens = aDataRow.split(" ");
                for (j = 0; j < tokens.length; j++) {
                    aux = tokens[j];
                    myint = Integer.parseInt(aux);
                    dbn_SELLO[i][j] = myint;
                }
                i++;
            }
            double total = dbn_SELLO[15][37];
            //String total2 = String.valueOf(total);
            myReader.close();
        } catch (Exception e) {
            Toast.makeText(getBaseContext(), e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }

        //////////////////////////
        // Read nod_SELLO
        /////////////////////////
        try {

            File myFile = new File("/sdcard/nodes.txt");
            i=0;
            FileInputStream fIn = new FileInputStream(myFile);
            BufferedReader myReader = new BufferedReader(
                    new InputStreamReader(fIn));
            String aDataRow = "";
            String aBuffer = "";
            while ((aDataRow = myReader.readLine()) != null) {
                aBuffer += aDataRow + "\n";
                String[] tokens = aDataRow.split(" ");
                for (j=0;j<tokens.length;j++) {
                    aux = tokens[j];
                    mydouble = Double.parseDouble(aux);
                    nod_aux_SELLO[i][j] = mydouble;
                    if (j == 0) {
                        nod_SELLO[i][j] = xScaleS * mydouble / xLengthS - 20;
                    }

                    if (j == 1)
                        nod_SELLO[i][j] = yScaleS * (yLengthS - mydouble) / yLengthS + 80;
                }
                i++;
            }
            double total = nod_SELLO[0][1];
            String total2 = String.valueOf(total);
            myReader.close();
        }
        catch (Exception e) {
            Toast.makeText(getBaseContext(), e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }

        //////////////////////////
        // Read parent_SELLO
        /////////////////////////
        try {

            File myFile = new File("/sdcard/conection.txt");
            i=0;
            FileInputStream fIn = new FileInputStream(myFile);
            BufferedReader myReader = new BufferedReader(
                    new InputStreamReader(fIn));
            String aDataRow = "";
            String aBuffer = "";
            while ((aDataRow = myReader.readLine()) != null) {
                aBuffer += aDataRow + "\n";
                String[] tokens = aDataRow.split(" ");
                for (j=0;j<tokens.length;j++) {
                    aux=tokens[j];
                    myint= Integer.parseInt(aux);
                    parent_SELLO[i][j]=myint;
                }
                i++;
            }
            myReader.close();
        }
        catch (Exception e) {
            Toast.makeText(getBaseContext(), e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private Integer distance(Integer successor, Integer node_goal) {
        Double dist;
        dist=Math.sqrt((nod_aux_SELLO[successor-1][0]-nod_aux_SELLO[node_goal-1][0])*(nod_aux_SELLO[successor-1][0]-nod_aux_SELLO[node_goal-1][0])+(nod_aux_SELLO[successor-1][1]-nod_aux_SELLO[node_goal-1][1])*(nod_aux_SELLO[successor-1][1]-nod_aux_SELLO[node_goal-1][1]));
        return dist.intValue();
    }

    private boolean pointIsInPath(PointF pointF){
        Integer closest_node=findClosestNode_Sello(pointF);
        if (path.contains(closest_node))
            return true;
        else
            return false;
    }

    @Override
    protected void onDestroy() {
        path.clear();
        super.onDestroy();
        mIALocationManager.destroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // starts receiving location updates
        mIALocationManager.requestLocationUpdates(IALocationRequest.create(), mLocationListener);
        mIALocationManager.registerRegionListener(mRegionListener);
        registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    @Override
    protected void onPause() {
        super.onPause();
        mIALocationManager.removeLocationUpdates(mLocationListener);
        mIALocationManager.unregisterRegionListener(mRegionListener);
        unregisterReceiver(onComplete);
    }

    /**
     * Methods for fetching floor plan data and bitmap image.
     * Method {@link #fetchFloorPlan(String id)} fetches floor plan data including URL to bitmap
     */

     /*  Broadcast receiver for floor plan image download */
    private BroadcastReceiver onComplete = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0L);
            if (id != mDownloadId) {
                Log.w(TAG, "Ignore unrelated download");
                return;
            }
            Log.w(TAG, "Image download completed");
            Bundle extras = intent.getExtras();
            DownloadManager.Query q = new DownloadManager.Query();
            q.setFilterById(extras.getLong(DownloadManager.EXTRA_DOWNLOAD_ID));
            Cursor c = mDownloadManager.query(q);

            if (c.moveToFirst()) {
                int status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
                if (status == DownloadManager.STATUS_SUCCESSFUL) {
                    // process download
                    String filePath = c.getString(c.getColumnIndex(
                            DownloadManager.COLUMN_LOCAL_FILENAME));
                    showFloorPlanImage(filePath);
                }
            }
            c.close();
        }
    };

    private void showFloorPlanImage(String filePath) {
        Log.w(TAG, "showFloorPlanImage: " + filePath);
        mImageView.setRadius(mFloorPlan.getMetersToPixels() * dotRadius);
        mImageView.setImage(ImageSource.uri(filePath));
    }

    /**
     * Fetches floor plan data from IndoorAtlas server. Some room for cleaning up!!
     */
    // Depending on the region you are the app will load the following map
    private void fetchFloorPlan(String id) {
        cancelPendingNetworkCalls();
        final IATask<IAFloorPlan> asyncResult = mFloorPlanManager.fetchFloorPlanWithId(id);
        mPendingAsyncResult = asyncResult;
        if (mPendingAsyncResult != null) {
            mPendingAsyncResult.setCallback(new IAResultCallback<IAFloorPlan>() {
                @Override
                public void onResult(IAResult<IAFloorPlan> result) {
                    Log.d(TAG, "fetch floor plan result:" + result);
                    if (result.isSuccess() && result.getResult() != null) {
                        mFloorPlan = result.getResult();
                        String fileName = mFloorPlan.getId() + ".img";
                        String filePath = Environment.getExternalStorageDirectory() + "/"
                                + Environment.DIRECTORY_DOWNLOADS + "/" + fileName;
                        File file = new File(filePath);
                        if (!file.exists()) {
                            DownloadManager.Request request =
                                    new DownloadManager.Request(Uri.parse(mFloorPlan.getUrl()));
                            request.setDescription("IndoorAtlas floor plan");
                            request.setTitle("Floor plan");
                            // requires android 3.2 or later to compile
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                                request.allowScanningByMediaScanner();
                                request.setNotificationVisibility(DownloadManager.
                                        Request.VISIBILITY_HIDDEN);
                            }
                            request.setDestinationInExternalPublicDir(Environment.
                                    DIRECTORY_DOWNLOADS, fileName);

                            mDownloadId = mDownloadManager.enqueue(request);
                        } else {
                            showFloorPlanImage(filePath);
                        }
                    } else {
                        // do something with error
                        if (!asyncResult.isCancelled()) {
                            Toast.makeText(ImageViewActivity.this,
                                    (result.getError() != null
                                            ? "error loading floor plan: " + result.getError()
                                            : "access to floor plan denied"), Toast.LENGTH_LONG)
                                    .show();
                        }
                    }
                }
            }, Looper.getMainLooper()); // deliver callbacks in main thread
        }
    }

    private void cancelPendingNetworkCalls() {
        if (mPendingAsyncResult != null && !mPendingAsyncResult.isCancelled()) {
            mPendingAsyncResult.cancel();
        }
    }


}

