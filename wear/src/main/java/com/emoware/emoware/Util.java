package com.emoware.emoware;


import android.net.Uri;

public class Util {
    public static Uri makeControlUri(String action, String data) {
        return Uri.parse(action + Constants.DATA_SEPARATOR + data);
    }

    public static String getAction(Uri uri) {
        return uri.getScheme();
    }

    public static String getData(Uri uri) {
        return uri.getSchemeSpecificPart();
    }
}
