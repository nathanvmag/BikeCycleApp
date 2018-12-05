package bikecycle.com.bikecycle;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import com.beardedhen.androidbootstrap.BootstrapCircleThumbnail;

import java.io.InputStream;

public class DownloadImageTask2 extends AsyncTask<String, Void, Bitmap> {
    BootstrapCircleThumbnail bmImage;

    public DownloadImageTask2(BootstrapCircleThumbnail bmImage) {
        this.bmImage = bmImage;
    }

    protected Bitmap doInBackground(String... urls) {
        String urldisplay = urls[0];
        Bitmap mIcon11 = null;
        try {
            InputStream in = new java.net.URL(urldisplay).openStream();
            mIcon11 = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            utils.log( e.getMessage());
            e.printStackTrace();
        }
        return mIcon11;
    }

    protected void onPostExecute(Bitmap result) {
        bmImage.setImageBitmap(result);
    }
}
