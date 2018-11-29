package bikecycle.com.bikecycle;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class utils {
    public static void log(Object tolog)
    {
        Log.d("BikeCycle",tolog.toString());
    }
    public static void toast(Context ctx,String text){
        Toast.makeText(ctx,text,Toast.LENGTH_LONG).show();}
}
