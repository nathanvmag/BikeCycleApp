package bikecycle.com.bikecycle;

public class Entrega {
    public String[] status;
    public String entregaID,dataa,starthora,empresaID,alocada;
    public int statusid=0,entregaid,clienteID;

    public Entrega(String entreid,String dt,String hr,int st,int entid,int md,String alo)
    {
        entregaID=entreid;
        dataa=fixdate(dt);
        starthora=hr.replace("-",":");
        statusid=st;
        entregaid=entid;
        empresaID=entregaID;
        alocada= alo;
        if(md==0)
        {
            status= new String[]{"Procurando Entregador","Aceito, aguarde o entregador","Enviado para entrega","Pedido finalizado","Cancelado"};
        }
        else{
            status= new String[]{"","Aguardando chegada ao local","Indo para entrega","Pedido finalizado","Cancelado"};

        }

    }
    String fixdate(String data)
    {
        String  dt = data.replace("-","/").replace("-","/");
        String[] tempdt= dt.split("/");
        dt = tempdt[2]+"/"+tempdt[1]+"/"+tempdt[0];
        return dt;
    }
}
