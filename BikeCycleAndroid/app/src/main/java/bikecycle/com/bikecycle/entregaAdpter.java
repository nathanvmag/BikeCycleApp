package bikecycle.com.bikecycle;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.BootstrapLabel;
import com.beardedhen.androidbootstrap.BootstrapProgressBar;
import com.beardedhen.androidbootstrap.api.defaults.DefaultBootstrapBrand;

import java.util.List;

public class entregaAdpter extends BaseAdapter {

    private final List<Entrega> entre;
    Context act ;
    public entregaAdpter(List<Entrega> cursos, Context act) {
        this.entre = cursos;
        this.act=act;
    }
    @Override
    public int getCount() {
        return entre.size();
    }

    @Override
    public Object getItem(int i) {
        return entre.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View v= ((Activity)viewGroup.getContext()) .getLayoutInflater().inflate(R.layout.entrega_history_list,viewGroup,false);
        Entrega ent= entre.get(i);
        ((TextView)v.findViewById(R.id.statustx)).setText(ent.status[ent.statusid]);
         utils.log("Status da entrega "+ ent.statusid);
        ((TextView)v.findViewById(R.id.datast)).setText(ent.dataa);
        ((TextView)v.findViewById(R.id.horast)).setText(ent.starthora);
        ((TextView)v.findViewById(R.id.pednum)).setText(""+ent.entregaid);
        ((TextView)v.findViewById(R.id.alocado)).setText((ent.alocada.equals("0")?"Avulso":(ent.alocada.equals("1")?"Alocada":"Alfa")));
        BootstrapProgressBar progressBar= (BootstrapProgressBar)v.findViewById(R.id.progbar);

        progressBar.setProgress(ent.statusid==4?4: ent.statusid+1);
        if(ent.statusid==0)progressBar.setBootstrapBrand(DefaultBootstrapBrand.WARNING);
        else if(ent.statusid<3)progressBar.setBootstrapBrand(DefaultBootstrapBrand.INFO);
        else progressBar.setBootstrapBrand(DefaultBootstrapBrand.SUCCESS);
        if(ent.statusid==4)
            progressBar.setBootstrapBrand(DefaultBootstrapBrand.DANGER);

        return v;
    }
}
