package edu.uclm.esi.disoft.comandas.dominio;

import org.json.JSONObject;

import edu.uclm.esi.disoft.comandas.etiquetas.JSONable;

import edu.uclm.esi.disoft.comandas.etiquetas.BSONable;


@BSONable
public class Mesa {
	@JSONable//anota campos a utilizar para escribir objetos json , unicamente se procesaran los campos jotasonables y ahorramos código , 
	//asi orientamos esto a una programación.SOLO ID para JSON
	//algo debe jotasonear el objeto plato(por ejemplo) y el jatoseneador mirara qe campos con JSON hay , sobre los datos JSON lee valor id,
	//nombre , precio y los pone en un objeto JSON
	private int _id;
	private Comanda comandaActual;
	public Mesa() {
		
	}
	public Mesa(int id) {
		this._id=id;
	}

	public JSONObject toJSONObject() {
		JSONObject jso=new JSONObject();
		jso.put("_id", this._id);
		jso.put("estado", comandaActual==null ? "Libre" : "Ocupada");
		return jso;
	}
	
	public boolean estaLibre() {
		return comandaActual==null;
	}

	public void abrir() throws Exception {
		if (comandaActual!=null)
			throw new Exception("La mesa ya está abierta. Elige otra");
		comandaActual=new Comanda();
	}

	public void cerrar() throws Exception {
		if (comandaActual==null)
			throw new Exception("La mesa ya está cerrada");
		comandaActual.cerrar();
		comandaActual=null;
	}

	public void addToComanda(Plato plato, int unidades) {
		this.comandaActual.add(plato, unidades);
	}

	public JSONObject estado() {
		JSONObject jso=new JSONObject();
		jso.put("id", this._id);
		if (comandaActual==null) 
			jso.put("estado", "Libre");
		else {
			jso.put("comanda", this.comandaActual.toJSONObject());
		}
		return jso;
	}
}
