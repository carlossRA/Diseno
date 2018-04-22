package edu.uclm.esi.disoft.comandas.etiquetas;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONObject;

import edu.uclm.esi.disoft.comandas.dominio.Comanda;
import edu.uclm.esi.disoft.comandas.dominio.Plato;

public class JSONeador {
	public static JSONObject toJSONObject(Object object) {
		
		JSONObject jso = new JSONObject();
		Class<?> clase =object.getClass();
		Field[] campos = clase.getDeclaredFields();//me da todos los campos de una clase definidos en una propia clase
		for(Field campo:campos)
		{
			if(!campo.isAnnotationPresent(JSONable.class))// si no esta la notación jsonable presente, continua
	        continue;	
			campo.setAccessible(true);
			try {
				String nombre=campo.getName();
	            Object valor=campo.get(object);
				if (Collection.class.isAssignableFrom(campo.getType())) {//la clase vector es una superclase del tipo del campo??¿?
					//si es una colecion iremos por cada objeto jotasoneando y colocandolo en ....
					//cogeremos como colección lo qe antes se cogia como objeto, el campo
					Collection<?> coleccion=(Collection) campo.get(object);
					JSONArray jsa=new JSONArray();
					for(Object objetito:coleccion) {
						jsa.put(toJSONObject(objetito));
					}
					
					jso.put(nombre, jsa);
				}
				else if(isJSONeable(valor)) {
					JSONable anotacion =campo.getAnnotation(JSONable.class);
					if(anotacion.nombre().length()==0&&anotacion.campo().length()==0)
					jso.put(nombre, toJSONObject(valor));
					else {
						String nombreCampoAsociado=anotacion.campo();//"_id"
						String nombreNuevo=anotacion.nombre();//"idPlato"
						Field campoAsociado=valor.getClass().getDeclaredField(nombreCampoAsociado);
						campoAsociado.setAccessible(true);
						Object valorCampoAsociado=campoAsociado.get(valor);
						jso.put(nombreNuevo, valorCampoAsociado);
						
					}
				}
						
					
				else {
				
			//	Object valor =campo.get(object);//oye campo dame el valor qe tienes en este objeto
				jso.put(nombre, valor);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			
		}
		//jso.put("nombre",object.getNombre());
		//en la siguiente vuelta del bucle el id 
		//en la siguiente el dato qe falte.....
		}
			return jso;
	
	}
	
	private static boolean isJSONeable(Object object) {//miraremos si la clase de un objeto tiene atributos con estiqeta JSONEABLE
		Class<?> clase =object.getClass();
		Field[] campos=clase.getDeclaredFields();
		for(Field campo:campos)
			if(campo.isAnnotationPresent(JSONable.class))
				return true;
		return false;
	}

	public static void main(String[]args) {
		Plato tortilla=new Plato ("22","Tortilla",6.50);
		System.out.println(JSONeador.toJSONObject(tortilla));
		
		Comanda comanda =new Comanda();
		comanda.add(tortilla, 3);
		System.out.println(JSONeador.toJSONObject(comanda));
		
	}
}

