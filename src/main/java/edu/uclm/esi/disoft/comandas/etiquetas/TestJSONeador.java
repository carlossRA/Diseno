package edu.uclm.esi.disoft.comandas.etiquetas;

import static org.junit.Assert.*;

import org.json.JSONObject;
import org.junit.Test;

import edu.uclm.esi.disoft.comandas.dominio.Comanda;
import edu.uclm.esi.disoft.comandas.dominio.Mesa;
import edu.uclm.esi.disoft.comandas.dominio.Plato;
import edu.uclm.esi.disoft.comandas.dominio.PlatoPedido;

public class TestJSONeador {

	@Test
	public void test() {
	Plato plato=new Plato("26","Tortilla",6.50);
	
	PlatoPedido platoPedido=new PlatoPedido(plato,3);
	JSONObject jso=JSONeador.toJSONObject(platoPedido);
	System.out.println(jso.toString());
	
	String valorEsperado=
			"{\"unidades\":3,\"idPlato\":\"26\"}";
	System.out.println(valorEsperado);//no son iguales todavía , qeremos qe sean iguales .
	assertEquals(jso.toString(),valorEsperado);
		/*	
		Plato plato=new Plato("1","Gazpacho",5);
		String a=plato.toJSONObject().toString();
		String b=JSONeador.toJSONObject(plato).toString();
		System.out.println(a);
		System.out.println(b);
		assertEquals(a,b);
	
	Mesa mesa =new Mesa(1);
	try {
		mesa.abrir();
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	mesa.addToComanda(plato, 2);
	System.out.println(mesa.toJSONObject());
	JSONObject jsoMesa=JSONeador.toJSONObject(mesa);
	jsoMesa.put("estado",mesa.estaLibre()? "Libre":"Ocupada");
	System.out.println(jsoMesa);
	System.out.println(JSONeador.toJSONObject(mesa));
	
   */
	}

}
