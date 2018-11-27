package bikecycle.com.bikecycle;

public class entregas {
    public String[] status= new String[]{"Procurando Entregador","Aceito, aguarde o entregador","Enviado para entrega","Pedido finalizado"};
    public String entregaID,dataa,starthora;
    public int statusid=0,entregaid;
    public entregas(String entreid,String dt,String hr,int st,int entid)
    {
        entregaID=entreid;
        dataa=fixdate(dt);
        starthora=hr.replace("-",":");
        statusid=st;
        entregaid=entid;
    }
    String fixdate(String data)
    {
        String  dt = data.replace("-","/").replace("-","/");
        String[] tempdt= dt.split("/");
        dt = tempdt[2]+"/"+tempdt[1]+"/"+tempdt[0];
        return dt;
    }
}
