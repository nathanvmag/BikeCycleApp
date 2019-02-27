package bikecycle.com.bikecycle;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import cz.msebera.android.httpclient.Header;

public class altersenha extends AppCompatActivity {
    String myId,tipe;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_altersenha);
        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            myId=extras.getString("id");
            tipe=extras.getString("tipe");
        }else {
            onBackPressed();
        }

        findViewById(R.id.alterabt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                String oldpass= ((EditText)findViewById(R.id.oldsenha)).getText().toString();
                String novasenha= ((EditText)findViewById(R.id.oldsenha2)).getText().toString();
                String novasenhaconf= ((EditText)findViewById(R.id.oldsenha3)).getText().toString();
                if(oldpass.trim().length()>0&&novasenha.trim().length()>0&&novasenhaconf.trim().length()>0)
                {
                    if(!novasenha.equals(novasenhaconf)){
                        Toast.makeText(getApplicationContext(),"As senhas não coincidem",Toast.LENGTH_LONG).show();
                        return;
                    }else{
                    RequestParams rp = new RequestParams();
                    rp.add("servID", "45");
                    rp.add("id",myId);
                    rp.add("oldpass",oldpass);
                    rp.add("newpass",novasenha);
                    rp.add("tipe",tipe);

                    view.setEnabled(false);
                    HttpUtils.postByUrl(loginPage.basesite + "application.php", rp, new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            view.setEnabled(true);

                            String resp = new String(responseBody);
                            utils.log(resp);
                            if(resp.equals("OK"))
                            {
                                Toast.makeText(getApplicationContext(), "Sucesso ao alterar a senha", Toast.LENGTH_LONG).show();
                                SharedPreferences pm = getSharedPreferences("pref",MODE_PRIVATE);
                                SharedPreferences.Editor editor= pm.edit();
                                editor.clear();
                                editor.commit();
                                startActivity(new Intent(getApplicationContext(),loginPage.class));


                            }else{
                                Toast.makeText(getApplicationContext(), "Falha ao alterar a senha, senha antiga incorreta"+resp ,Toast.LENGTH_LONG).show();

                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                            view.setEnabled(true);

                            Toast.makeText(getApplicationContext(), "Erro ao alterar senha " + responseBody, Toast.LENGTH_LONG).show();
                        }
                    });
                }
                }
                else Toast.makeText(getApplicationContext(),"Por favor preencha todos os campos",Toast.LENGTH_LONG).show();
            }
        });
    }
}
