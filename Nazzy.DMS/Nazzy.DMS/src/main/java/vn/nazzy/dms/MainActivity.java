package vn.nazzy.dms;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.provider.Settings;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.GeolocationPermissions;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Locale;

import vn.nazzy.dms.MobileNumber.MobileNumber;
import vn.nazzy.dms.UpdateCheckerer.UpdateChecker;

public class MainActivity extends Activity {
    private static final int PERIOD = 2000;
    private final static int FILE_CHOOSER_RESULT_CODE = 1;
    private static final String PIC_DIRECTORY = Environment.DIRECTORY_DCIM;
    private static final String URL_VERSION = "http://pm.nazzy.vn/dl/version.txt";
    private static final String URL_APK = "http://pm.nazzy.vn/dl/dms.apk";
    public GPSTracker gps;
    public ProgressDialog progressBar;
    public WebView wv;
    public String aa = "";
    public String imageFilePath = "";
    public Context mContext;
    private boolean chkStatus = false;
    private long lastPressedTime;
    private ValueCallback<Uri> mUploadMessage;
    private Uri imageUri;


    /**
     * public static String getDomainName(String url) {
     * if (!url.startsWith("http") && !url.startsWith("https")) {
     * url = "http://" + url;
     * }
     * try {
     * URL netUrl = new URL(url);
     * String host = netUrl.getHost();
     * if (host.startsWith("www")) {
     * host = host.substring("www".length() + 1);
     * }
     * return host;
     * } catch (MalformedURLException e) {
     * e.printStackTrace();
     * return "";
     * }
     * }
     */
    public static String getAddressFromLocation(double latitude,
                                                double longitude, Context context) {

        String locationAddress = "Địa chỉ thực hiện:";

        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {

            List<Address> addresses = geocoder.getFromLocation(latitude,
                    longitude, 1);

            if (addresses != null && !addresses.isEmpty()) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder(
                        "Địa chỉ thực hiện:\n");
                for (int i = 0; i < returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress
                            .append(returnedAddress.getAddressLine(i));
                    if (i != returnedAddress.getMaxAddressLineIndex() - 1) {
                        strReturnedAddress.append(" - ");
                    }
                }
                locationAddress = strReturnedAddress.toString();

                // locationAddress = locationAddress.replace("Address:", "");

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return locationAddress;
    }

    private void showAttachmentDialog(ValueCallback<Uri> uploadMsg) {
        this.mUploadMessage = uploadMsg;

        File imageStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(PIC_DIRECTORY),
                "NAZZY");
        if (!imageStorageDir.exists()) {
            imageStorageDir.mkdirs();
        }
        File file = new File(imageStorageDir + File.separator + "IMG_NAZZY.jpg");
        // + String.valueOf(System.currentTimeMillis()) + ".jpg");
        this.imageUri = Uri.fromFile(file); // save to the private variable

        final Intent captureIntent = new Intent(
                MediaStore.ACTION_IMAGE_CAPTURE);
        captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);

        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("image/*");

        Intent chooserIntent = Intent.createChooser(i,
                "Chọn cách lây hình ảnh...");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS,
                new Parcelable[]{captureIntent});
        this.startActivityForResult(chooserIntent, FILE_CHOOSER_RESULT_CODE);

    }

    // End Update 04/10/2013

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private String getRealPathFromURI(Uri contentUri) {
        if (contentUri == null) {
            return "";
        }
        String[] proj = {MediaColumns.DATA};
        CursorLoader loader = new CursorLoader(mContext,
                contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaColumns.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    @TargetApi(Build.VERSION_CODES.FROYO)
    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent intent) {
        if (requestCode == FILE_CHOOSER_RESULT_CODE) {
            if (null == this.mUploadMessage) {
                return;
            }

            Uri result;
            String filename = "";
            String dir = Environment
                    .getExternalStoragePublicDirectory(PIC_DIRECTORY)
                    + File.separator + "NAZZY";
            imageFilePath = dir + File.separator + "IMG_NAZZY.jpg";
            if (resultCode != RESULT_OK) {
                result = null;
            } else {
                // result = intent == null ? this.imageUri : intent.getData();

                if (intent == null) {
                    result = this.imageUri;
                    filename = dir + File.separator + "IMG_NAZZY.jpg";
                } else {
                    result = intent.getData();
                    filename = getRealPathFromURI(result);
                }
            }

            try {
                Bitmap bp = BitmapHelper.decodeFile(filename);
                OutputStream outputStream = null;
                outputStream = new FileOutputStream(filename);
                bp.compress(Bitmap.CompressFormat.JPEG, 90, outputStream);
                bp.recycle();
                bp = null;
                outputStream.flush();
                outputStream.close();
                outputStream = null;
                Runtime.getRuntime().gc();


/**
 BitmapFactory.Options options = new BitmapFactory.Options();
 options.inPurgeable = true;
 options.outHeight = 50;
 options.outWidth = 50;
 options.inSampleSize = 4;
 System.gc();
 Bitmap bp = BitmapHelper.decodeFile(new File(filename),
 REQUIRED_SIZE, REQUIRED_SIZE, true);
 OutputStream outputStream = null;
 outputStream = new FileOutputStream(filename);
 bp.compress(Bitmap.CompressFormat.JPEG, 50, outputStream);
 bp.recycle();
 bp = null;
 outputStream.flush();
 outputStream.close();
 outputStream = null;
 Runtime.getRuntime().gc();
 **/

            } catch (IOException e) {
            } catch (Exception e) {
            }

            this.mUploadMessage.onReceiveValue(result);
            this.mUploadMessage = null;
        } else {
            return;
        }
    }

    public void showDialogToLocation() {

        AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(this);

        alertDialog2.setTitle("Thông tin quan trọng...");

        alertDialog2
                .setMessage("Chọn YES để thiết lập chức năng đặt đơn hàng, chọn NO để thoát khỏi chương trình?");

        alertDialog2.setPositiveButton("YES",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(
                                Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        chkStatus = getLocationAccess();
                        if (!chkStatus) {
                            finish();
                        }

                    }
                });
        alertDialog2.setNegativeButton("NO",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(mContext,
                                "Thiết lập này không thể gửi được đơn hàng...",
                                Toast.LENGTH_SHORT).show();
                        finish();
                        dialog.cancel();
                    }
                });

        alertDialog2.show();

    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        progressBar = ProgressDialog.show(MainActivity.this, "",
                "Hệ thống đang xử lý dữ liệu, xin hãy đợi...");

        // Update 10/10/2013
        mContext = getApplicationContext();
        AppPreferences _appPrefs = new AppPreferences(mContext);

        String someString = _appPrefs.getSmsBody();
        if (someString.equals("") || someString.length() < 4) {
            Toast.makeText(mContext,
                    "Không có cấu hình, thực hiện thiết lập cấu hình",
                    Toast.LENGTH_LONG).show();
            Intent i = new Intent(MainActivity.this, SettingActivity.class);
            startActivity(i);
        } else {
            aa = someString.trim();
        }
        //Kiểm tra version hệ thống
        UpdateChecker updateChecker = new UpdateChecker(this, false);
        updateChecker.checkForUpdateByVersionCode(URL_VERSION);
        if (updateChecker.isUpdateAvailable()) {
            updateChecker.downloadAndInstall(URL_APK);
        }

        if (!getLocationAccess()) {

            showDialogToLocation();

        }
        // Lấy giá trị Lat & Lon từ GPS Tracker
        gps = new GPSTracker(MainActivity.this);
        if (gps.canGetLocation && gps.location != null) {
            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();
            String addrs = getAddressFromLocation(latitude, longitude, this);
            Toast.makeText(mContext, addrs, Toast.LENGTH_LONG)
                    .show();
        } else {
            Toast.makeText(mContext, "Thiết bị chưa được định vị, khởi động lại máy...", Toast.LENGTH_SHORT).show();
        }
        //Get phone number
        MobileNumber mb = new MobileNumber(this);
        aa = aa + "\\" + mb.getMy10DigitPhoneNumber();
        mb.setDataUrl(aa);
        //End of update

        wv = (WebView) findViewById(R.id.webView1);

        // Thiết lập cho WebView chính xác như websetting

        wv.getSettings().setJavaScriptEnabled(true);
        wv.getSettings().setGeolocationEnabled(true);
        wv.getSettings().setDatabaseEnabled(true);
        wv.getSettings().setDomStorageEnabled(true);
        //wv.getSettings().setDatabasePath("/data/data/DMSNazzy/databases");
        wv.getSettings().setGeolocationDatabasePath("/data/data/databases/Nazzy/");
        wv.getSettings().setSaveFormData(true);
        wv.getSettings().setLoadWithOverviewMode(true);
        wv.getSettings().setUseWideViewPort(true);

        wv.setWebChromeClient(new WebChromeClient() {
            // For Android > 4.1
            public void openFileChooser(ValueCallback<Uri> uploadMsg,
                                        String acceptType, String capture) {
                openFileChooser(uploadMsg);
            }

            // Andorid 3.0 +
            public void openFileChooser(ValueCallback<Uri> uploadMsg,
                                        String acceptType) {
                openFileChooser(uploadMsg);
            }

            // Android 3.0

            public void openFileChooser(ValueCallback<Uri> uploadMsg) {
                mUploadMessage = uploadMsg;
                showAttachmentDialog(uploadMsg);
            }

            @Override
            public void onGeolocationPermissionsShowPrompt(String origin,
                                                           GeolocationPermissions.Callback callback) {
                callback.invoke(origin, true, false);
            }

        });
        wv.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                if (errorCode == ERROR_CONNECT || errorCode == ERROR_HOST_LOOKUP || errorCode == ERROR_TIMEOUT) {

                    //String data = "<div style=\"font-size:30px;color:red;;\">Không thể kết nối đến hệ thống bán hàng, kiểm tra lại Wifi/3G...</div>";
                    //view.loadData(Uri.encode("<html>" + data + "</html>"), "text/html", "UTF-8");
                    Toast.makeText(mContext, "Không thể kết nối đến máy chủ dịch vụ, kiểm tra lại cấu hình hoặc Wifi/3G...", Toast.LENGTH_SHORT).show();
                    view.loadUrl("about:blank");
                }
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                progressBar.show();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                if (progressBar.isShowing()) {
                    progressBar.dismiss();
                }
            }
        });

        try {
            wv.loadUrl(aa);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    // Press KeyBack to Exit

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.itemRefresh:
                wv.reload();
                break;
            case R.id.itemGoBack:
                if (wv.canGoBack()) {
                    wv.goBack();
                }
                break;
            case R.id.itemGoForward:
                if (wv.canGoForward()) {
                    wv.goForward();
                }
                break;
            case R.id.itemExit:
                this.finish();
            case R.id.itemConfig:
                Intent i = new Intent(MainActivity.this, SettingActivity.class);
                startActivity(i);
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean getLocationAccess() {
        String locationProviders = Settings.Secure.getString(
                getContentResolver(),
                Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        if (locationProviders == null || locationProviders.equals("")) {
            return false;
        }
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            switch (event.getAction()) {
                case KeyEvent.ACTION_DOWN:
                    if (event.getDownTime() - lastPressedTime < PERIOD) {
                        finish();
                    } else {
                        Toast.makeText(mContext,
                                "Gõ phím BACK lần nữa để thoát!...",
                                Toast.LENGTH_SHORT).show();
                        lastPressedTime = event.getEventTime();
                    }
                    return true;
            }
        }
        return false;
    }

}


