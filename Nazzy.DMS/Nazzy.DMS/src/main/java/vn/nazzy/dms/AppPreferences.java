package vn.nazzy.dms;

import android.content.Context;
import android.content.SharedPreferences;

public class AppPreferences {
    public static final String KEY_PREFS_SMS_BODY = "DMS_SETTING";
    private static final String APP_SHARED_PREFS = AppPreferences.class
            .getSimpleName(); // Name of the file -.xml
    private SharedPreferences _sharedPrefs;
    private SharedPreferences.Editor _prefsEditor;

    public AppPreferences(Context context) {
        this._sharedPrefs = context.getSharedPreferences(APP_SHARED_PREFS,
                Context.MODE_PRIVATE);
        this._prefsEditor = _sharedPrefs.edit();
    }

    public String getSmsBody() {
        return _sharedPrefs.getString(KEY_PREFS_SMS_BODY, "");
    }

    public void saveSmsBody(String text) {
        _prefsEditor.putString(KEY_PREFS_SMS_BODY, text);
        _prefsEditor.commit();
    }
}
