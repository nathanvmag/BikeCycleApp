package bikecycle.com.bikecycle;

public class Entrega {
    public String[] status;
    public String entregaID,dataa,starthora,empresaID;
    public int statusid=0,entregaid;

    public Entrega(String entreid,String dt,String hr,int st,int entid,int md)
    {
        entregaID=entreid;
        dataa=fixdate(dt);
        starthora=hr.replace("-",":");
        statusid=st;
        entregaid=entid;
        empresaID=entregaID;
        if(md==0)
        {
            status= new String[]{"Procurando Entregador","Aceito, aguarde o entregador","Enviado para entrega","Pedido finalizado"};
        }
        else{
            status= new String[]{"","Aguardando chegada ao local","Indo para entrega","Pedido finalizado"};

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
