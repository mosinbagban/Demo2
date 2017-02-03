package com.zainsoft.ramzantimetable.util;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by MB00354042 on 2/2/2017.
 */
public class Utility {

    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}
