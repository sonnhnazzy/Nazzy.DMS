package vn.nazzy.dms.MobileNumber;

import android.content.Context;
import android.telephony.TelephonyManager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * Created by sonnh_000 on 11/7/13.
 */
public class MobileNumber {
    private Context myContext;

    public MobileNumber(Context mContext) {
        myContext = mContext;
    }

    private String getMyPhoneNumber() {
        TelephonyManager mTelephonyMgr;
        mTelephonyMgr = (TelephonyManager) myContext.getSystemService(Context.TELEPHONY_SERVICE);
        return mTelephonyMgr.getLine1Number();
    }

    public String getMy10DigitPhoneNumber() {
        String s = getMyPhoneNumber();
        if (s == null) s = "0000000000";
        //if (s.substring(0,2).equals("+84")) s="0"+s.substring(3);
        s = s.replace("+84", "0");
        return s;
    }

    public void setDataUrl(String url) {
        // Making HTTP request
        String result = "-1";
        try {
            // defaultHttpClient
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);
            // HttpGet httpGet = new HttpGet(url);

            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();

            //is = httpEntity.getContent();
            //result = is.toString().trim();
            result = EntityUtils.toString(httpEntity).trim();
            

        } catch (UnsupportedEncodingException e) {
            // Log.d("Nhu cut 2", "OK");
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            // Log.d("Nhu cut 3", "OK");
            e.printStackTrace();
        } catch (IOException e) {
            // Log.d("Nhu cut 4", "OK");
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
