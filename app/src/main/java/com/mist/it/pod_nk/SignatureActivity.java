package com.mist.it.pod_nk;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.mist.it.pod_nk.MyConstant.urlSaveSignature;
import static com.mist.it.pod_nk.MyConstant.urlUploadPicture;

public class SignatureActivity extends AppCompatActivity {

    @BindView(R.id.edtSAFullName)
    EditText fullNameEditText;
    @BindView(R.id.linSACanvas)
    LinearLayout canvasLinearLayout;
    @BindView(R.id.btnSASave)
    Button saveButton;
    @BindView(R.id.btnSAClear)
    Button clearButton;
    signature mSignature;
    Bitmap mBitmap;


    View mView;
    String stringStoreId, SignName, jobNoString;
    String[] loginStrings;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signature);
        ButterKnife.bind(this);

        loginStrings = getIntent().getStringArrayExtra("Login");
        jobNoString = getIntent().getStringExtra("SubJobNo");
        stringStoreId = getIntent().getStringExtra("StoreId");


        mSignature = new signature(this, null);
        mSignature.setBackgroundColor(Color.WHITE);
        canvasLinearLayout.addView(mSignature, ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT);
        saveButton.setEnabled(false);
        mView = canvasLinearLayout;
        clearButton.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {
                Log.v("log_tag", "Panel Clear");
                mSignature.clear();
//                clearButton.setEnabled(false);
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.v("log_tag", "Panel Saved");
                boolean error = captureSignature();
                if (!error) {
                    mView.setDrawingCacheEnabled(true);
                    mSignature.save(mView);
                    Bundle b = new Bundle();
                    b.putString("status", "done");
                    Intent intent = new Intent();
                    intent.putExtras(b);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });


    }

    private boolean captureSignature() {
        boolean error = false;
        String errorMessage = "";


        if (fullNameEditText.getText().toString().equalsIgnoreCase("")) {
            errorMessage = errorMessage + getResources().getString(R.string.err_not_name);
            error = true;
        }

        if (error) {
            Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.TOP, 105, 50);
            toast.show();
        }

        return error;
    }

    private class SynUploadImage extends AsyncTask<Void, Void, String> {
        Context context;
        Bitmap bitmap;
        UploadImageUtils uploadImageUtils;
        String mUploadedFileName, signNameString;


        public SynUploadImage(Context context, Bitmap bitmap, String signNameString) {
            this.context = context;
            this.bitmap = bitmap;
            this.signNameString = signNameString;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... voids) {
            final String[] time = new String[1];
            GPSManager gpsManager = new GPSManager(SignatureActivity.this);
            uploadImageUtils = new UploadImageUtils();
            mUploadedFileName = "signature.jpg";
            time[0] = gpsManager.getDateTime();

            Log.d("Data", mUploadedFileName);
            Log.d("Data", stringStoreId);
            Log.d("Data", bitmap.toString());
            Log.d("Data", urlUploadPicture);

            final String result = UploadImageUtils.uploadFile(mUploadedFileName, urlUploadPicture, bitmap, stringStoreId, "S", jobNoString, "");
            Log.d("TAG", "Do in back after save:-->" + result);
            Log.d("TAG", "TIME ==>" + time[0]);
            Log.d("TAG", String.valueOf(result.equals("NOK")));
            if (result.equals("NOK")) {
                return "NOK";
            } else {
                try {
                    Log.d("Tag", stringStoreId + " , " + signNameString + " , " + result + " , " + loginStrings[5] + " , " + time[0]);
                    OkHttpClient okHttpClient = new OkHttpClient();
                    RequestBody requestBody = new FormEncodingBuilder()
                            .add("isAdd", "true")
                            .add("StoreId", stringStoreId)
                            .add("SignName", signNameString)
                            .add("File_Name", result)
                            .add("user_name", loginStrings[5])
                            .add("timeStamp", time[0])
                            .build();
                    Request.Builder builder = new Request.Builder();
                    Request request = builder.url(urlSaveSignature).post(requestBody).build();
                    Response response = okHttpClient.newCall(request).execute();
                    return response.body().string();
                } catch (Exception e) {
                    Log.d("TAG", String.valueOf(e));
                    return "NOK";
                }
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            Log.d("TAG", "JSON_Upload ==> " + s);
            if (s.equals("OK")) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, getResources().getText(R.string.save_comp), Toast.LENGTH_SHORT).show();
                    }
                });

            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, getResources().getText(R.string.save_incomp), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    public class signature extends View {

        static final float STROKE_WIDTH = 5f;
        static final float HALF_STROKE_WIDTH = STROKE_WIDTH / 2;
        Paint paint = new Paint();
        Path path = new Path();

        float lastTouchX;
        float lastTouchY;
        final RectF dirtyRect = new RectF();


        public signature(Context context, AttributeSet attrs) {
            super(context, attrs);
            paint.setAntiAlias(true);
            paint.setColor(Color.BLACK);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setStrokeWidth(STROKE_WIDTH);
        }

        public void save(View v) {
            Log.v("log_tag", "Width: " + v.getWidth());
            Log.v("log_tag", "Height: " + v.getHeight());
            if (mBitmap == null) {
                mBitmap = Bitmap.createBitmap(canvasLinearLayout.getWidth(), canvasLinearLayout.getHeight(), Bitmap.Config.RGB_565);

                SignName = fullNameEditText.getText().toString();

                Canvas canvas = new Canvas(mBitmap);
                try {

                    v.draw(canvas);
                    Log.v("log_tag", "Bitmap=++++++++++++++: " + mBitmap);

                    SynUploadImage synUploadImage = new SynUploadImage(SignatureActivity.this, mBitmap, SignName);
                    synUploadImage.execute();

                } catch (Exception e) {
                    Log.v("log_tag", e.toString());
                }
            }//end if
        }

        public void clear() {
            path.reset();
            invalidate();
            saveButton.setEnabled(false);

        }


        @Override
        protected void onDraw(Canvas canvas) {
            canvas.drawPath(path, paint);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float eventX = event.getX();
            float eventY = event.getY();
            saveButton.setEnabled(true);

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    path.moveTo(eventX, eventY);
                    lastTouchX = eventX;
                    lastTouchY = eventY;
                    return true;

                case MotionEvent.ACTION_MOVE:

                case MotionEvent.ACTION_UP:

                    resetDirtyRect(eventX, eventY);
                    int historySize = event.getHistorySize();
                    for (int i = 0; i < historySize; i++) {
                        float historicalX = event.getHistoricalX(i);
                        float historicalY = event.getHistoricalY(i);
                        expandDirtyRect(historicalX, historicalY);
                        path.lineTo(historicalX, historicalY);
                    }
                    path.lineTo(eventX, eventY);
                    break;

                default:
                    debug("Ignored touch event: " + event.toString());
                    return false;
            }

            invalidate((int) (dirtyRect.left - HALF_STROKE_WIDTH),
                    (int) (dirtyRect.top - HALF_STROKE_WIDTH),
                    (int) (dirtyRect.right + HALF_STROKE_WIDTH),
                    (int) (dirtyRect.bottom + HALF_STROKE_WIDTH));

            lastTouchX = eventX;
            lastTouchY = eventY;

            return true;
        }

        private void debug(String string) {
        }

        private void expandDirtyRect(float historicalX, float historicalY) {
            if (historicalX < dirtyRect.left) {
                dirtyRect.left = historicalX;
            } else if (historicalX > dirtyRect.right) {
                dirtyRect.right = historicalX;
            }

            if (historicalY < dirtyRect.top) {
                dirtyRect.top = historicalY;
            } else if (historicalY > dirtyRect.bottom) {
                dirtyRect.bottom = historicalY;
            }
        }

        private void resetDirtyRect(float eventX, float eventY) {
            dirtyRect.left = Math.min(lastTouchX, eventX);
            dirtyRect.right = Math.max(lastTouchX, eventX);
            dirtyRect.top = Math.min(lastTouchY, eventY);
            dirtyRect.bottom = Math.max(lastTouchY, eventY);
        }

    }
}
