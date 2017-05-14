package com.example.n_u.officebotapp.helpers;

import android.app.ProgressDialog;
import android.content.Context;
import android.view.Gravity;

public final class Progress {
    private static ProgressDialog pro;

    public static boolean IsShowing() {
        return pro.isShowing();
    }

    public static void Cancel() {
        pro.cancel();
    }

    public static void Show(Context paramContext) {
        pro = new ProgressDialog(paramContext);
        if (!pro.isShowing()) {
            if (pro.getWindow() != null) {
                pro.getWindow().setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL | Gravity.AXIS_SPECIFIED);
            }
            pro.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pro.setMessage("Loading...");
            pro.setIndeterminate(true);
            pro.setCancelable(true);
            pro.show();
        } else
            pro.cancel();
    }

    public static void Show(Context paramContext, String paramString) {
        pro = new ProgressDialog(paramContext);
        if (!pro.isShowing()) {
            if (pro.getWindow() != null) {
                pro.getWindow().setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL | Gravity.AXIS_SPECIFIED);
            }
            pro.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pro.setMessage(paramString);
            pro.setIndeterminate(true);
            pro.setCancelable(true);
            pro.show();
        } else
            pro.cancel();
    }

    public static ProgressDialog getPro() {
        return pro;
    }

    public static void setPro(ProgressDialog paramProgressDialog) {
        pro = paramProgressDialog;
    }
}



/* Location:           C:\Users\N_U\Desktop\Tolo\ob\classes30-dex2jar.jar

 * Qualified Name:     com.example.n_u.officebot.helpers.Progress

 * JD-Core Version:    0.7.0.1

 */