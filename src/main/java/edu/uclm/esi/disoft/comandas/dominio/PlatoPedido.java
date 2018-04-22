package edu.uclm.esi.disoft.comandas.dominio;

import org.json.JSONObject;

import edu.uclm.esi.disoft.comandas.etiquetas.BSONable;
import edu.uclm.esi.disoft.comandas.etiquetas.JSONable;

public class PlatoPedido {
	@JSONable(campo="_id",nombre="idPlato")
	@BSONable(campo="_id",nombre="idPlato",OnDeleteCascade=true)
	private Plato plato;
	@JSONable// es comodo cambiar información en json(asi me devuelve el server) , el metodo toJSONObject lo hemos escrito muchas veces,
	//tambien hay un método asi en Comanda , en plato tambien hay un toJSONObject. En vez de meter siempre este metodo en 
	//todas las clases para intercambiar con el cliente nos ponemos una etiqeta para anotar campos a pasar al JSONOBJETC.aNOTAMOS LOS CAMPOS
	//QE NOS VAMOS A LLEVAR EN EL MÉTODO JSONObject
	
	private int unidades;
	public PlatoPedido()
	{
		
	}
	public PlatoPedido(Plato plato, int unidades) {
		this.plato=plato;
		this.unidades=unidades;
	}

	public JSONObject toJSONObject() {
		JSONObject jso=this.plato.toJSONObject();
		jso.put("unidades", this.unidades);
		return jso;
	}
}
