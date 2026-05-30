package com.tajim.urlshortener.utils;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;

public class AppUtils {

    public static void copyToClipBoard(Context context, String text) {
        ClipboardManager clipboard =
                (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);

        ClipData clip = ClipData.newPlainText("copied_text", text);
        clipboard.setPrimaryClip(clip);
    }
}