package bikecycle.com.bikecycle;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static bikecycle.com.bikecycle.loginPage.basesite;

public class utils {
    public static void log(Object tolog)
    {
        Log.d("BikeCycle",tolog.toString());
    }
    public static void toast(Context ctx,String text) {
        try {
            Toast.makeText(ctx, text, Toast.LENGTH_LONG).show();
        }catch (Exception ex){

        }
    }
    public static void noInternetLog(Context at,View mainlayout)
    {
                try {
                    LayoutInflater inflater = (LayoutInflater)
                            at.getSystemService(LAYOUT_INFLATER_SERVICE);
                    final View popupView = inflater.inflate(R.layout.nointernet, null);

                    // create the popup window
                    int width = LinearLayout.LayoutParams.MATCH_PARENT;
                    int height = LinearLayout.LayoutParams.WRAP_CONTENT;
                    boolean focusable = false; // lets taps outside the popup also dismiss it
                    final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

                    // show the popup window
                    // which view you pass in doesn't matter, it is only used for the window tolken
                    popupWindow.showAtLocation(mainlayout, Gravity.TOP, 0, 0);
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            popupWindow.dismiss();
                        }
                    },6000);

                }
                 catch (Exception e){
                    utils.log("FALHA AO ABRIR ");
                 }

    }
}
