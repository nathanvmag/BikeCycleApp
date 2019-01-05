package bikecycle.com.bikecycle;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final Activity at = (Activity)this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        Handler handle = new Handler();
        handle.postDelayed(new Runnable() {
            @Override
            public void run() {
                mostrarLogin(at);
            }
        }, 2000);
    }
    void mostrarLogin(Activity at)
    {
        Intent a = new Intent(at
                ,loginPage.class);
        startActivity(a);
        finish();
    }

}
