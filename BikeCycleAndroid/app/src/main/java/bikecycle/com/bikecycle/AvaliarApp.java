package bikecycle.com.bikecycle;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RatingBar;

import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import cz.msebera.android.httpclient.Header;

import static bikecycle.com.bikecycle.loginPage.basesite;

public class AvaliarApp extends AppCompatActivity {
    String myid,tipe;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_avaliar_app);
        if (getIntent().getExtras()!=null) {
            Bundle extras = getIntent().getExtras();
            myid = extras.getString("id");
            tipe = extras.getString("tipe");
            findViewById(R.id.envia2).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String avaliatx= ((BootstrapEditText)findViewById(R.id.reclamabody2)).getText().toString();
                    float valuenota = ((RatingBar)findViewById(R.id.nota)).getRating();
                    RequestParams rps = new RequestParams();
                    rps.add("servID","760");
                    rps.add("id",myid+"");
                    rps.add("mess",avaliatx);
                    rps.add("from",tipe+"");
                    rps.add("nota",valuenota+"");
                    HttpUtils.postByUrl(basesite + "application.php", rps, new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            String res= new String(responseBody);
                            if (res.equals("OK"))
                            {
                                utils.toast(getApplicationContext(),"Sucesso ao Enviar Avaliação ");
                                onBackPressed();

                            }
                            else{
                                utils.toast(getApplicationContext(),"Falha ao Enviar Avaliação "+ new String(responseBody));

                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                            utils.toast(getApplicationContext(),"Falha ao Enviar Avaliação "+ new String(responseBody));
                        }
                    });
                }
            });
        }

    }
}
