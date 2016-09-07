package com.example.sec.whereami;

import android.view.View;

/**
 * Created by CodeFactory on 2016-08-11.
 */
public class AccessibilitySetting {
    public static void setAccessibilityIgnore(View view) {
        view.setClickable(false);
        view.setFocusable(false);
        view.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO);
    }

    public static void setAccessibilitySetting(View view) {
        view.setClickable(true);
        view.setFocusable(true);
        view.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_YES);
    }
}
