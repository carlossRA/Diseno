package edu.uclm.esi.disoft.comandas.etiquetas;

import java.lang.reflect.Field;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import org.bson.BsonDocument;
import org.bson.BsonDouble;
import org.bson.BsonInt32;
import org.bson.BsonInt64;
import org.bson.BsonString;
import org.bson.BsonValue;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;

import edu.uclm.esi.disoft.comandas.dao.MongoBroker;
import edu.uclm.esi.disoft.comandas.dominio.Mesa;
import edu.uclm.esi.disoft.comandas.dominio.Plato;
import edu.uclm.esi.disoft.comandas.dominio.PlatoPedido;

public class BSoneador {
	public static void insert(Object objeto)throws Exception{
		/*
		 * 1.Traducir objeto a un bso
		 *   1.1 Por reflexión, leer los campos del objeto ,leer su valor y colocarlo en bso
		 * 2. Acceder a una colección que se llame igual qe la clase del objeto 
		 *  3. A través del MongBroker, insertar el bso que se acaba de construir en la coleccion 
		 * 
		 * 
		 */
		
		Class<?> clase =objeto.getClass();
		Field [] campos=clase.getDeclaredFields();
		BsonDocument bso=new BsonDocument();
		for(int i=0; i < campos.length;i++) {
			Field campo=campos[i];
			campo.setAccessible(true);
			Object valor =campo.get(objeto);
			if(valor==null)
				continue;
			BSONable anotacion=campo.getAnnotation(BSONable.class);
			if(anotacion==null)
				bso.append(campo.getName(), getBsonValue(valor));
			else {
				String nombreCampoAsociado=anotacion.campo();
				String nombreNuevo=anotacion.nombre();
				Field campoAsociado=valor.getClass().getDeclaredField(nombreCampoAsociado);
				campoAsociado.setAccessible(true);
				Object valorCampoAsociado=campoAsociado.get(valor);
				bso.append(nombreNuevo, getBsonValue(valorCampoAsociado));
			}
		}
		MongoCollection<BsonDocument> coleccion=MongoBroker.get().getCollection(clase.getName());
		coleccion.insertOne(bso);
	}
	
	private static BsonValue getBsonValue(Object valorDelCampo) {
		Class<? extends Object> tipo=valorDelCampo.getClass();
		if(tipo==int.class||tipo==Integer.class) 
			return new BsonInt32((int)valorDelCampo);
		if(tipo==long.class||tipo==long.class) 
			return new BsonInt64((long)valorDelCampo);
		if (tipo==double.class||tipo==Double.class)
			return new BsonDouble((double)valorDelCampo);
		if(tipo==String.class)
			return new BsonString(valorDelCampo.toString());
		if(tipo.isAnnotationPresent(BSONable.class)) {
				
			Field []campos =valorDelCampo.getClass().getDeclaredFields();
			BsonDocument bso=new BsonDocument();
			for(int i =0;i< campos.length;i++) {
				Field campo=campos[i];
				campo.setAccessible(true);
				try {
					bso.put(campo.getName(),getBsonValue(campo.get(valorDelCampo)));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				
			}
		}
		
	}
		
		return null;
	}
	public static ConcurrentHashMap<Object,Object> load(Class<?>clase) throws Exception{
		ConcurrentHashMap<Object,Object> result=new ConcurrentHashMap<>();
		MongoCollection<BsonDocument> coleccion=MongoBroker.get().getCollection(clase.getName());
		MongoCursor<BsonDocument> fi=coleccion.find().iterator();
		while(fi.hasNext()) {
	
			BsonDocument bso=fi.next();
	        Object objeto=getObject(clase,bso);
	        result.put(getId(bso), objeto);
		}
		return result;
		
	
}

	private static Object getId(BsonDocument bso) {
		if(bso.get("_id").isString())
		return bso.get("_id").asString().getValue();
		if(bso.get("_id").isInt32())
		return bso.get("_id").asInt32().getValue();
		
		return null;
	}

	private static Object getObject(Class<?> clase, BsonDocument bso) throws Exception {
		Object result=clase.newInstance();//creo de manera reflexiva una instancia. es como hacer Mesa result= new Mesa() 
	    Iterator <String>nombresDeLosCampos=bso.keySet().iterator();
		while(nombresDeLosCampos.hasNext()) {
			String nombreDeCampo=nombresDeLosCampos.next();
			Field campo=clase.getDeclaredField(nombreDeCampo);
			if(campo==null)
			
				continue;
				campo.setAccessible(true);
				BsonValue valorDelBson=bso.get(nombreDeCampo);
				
				set(campo,result,valorDelBson);
		}
			
		
	    
	    return result;
	}
   /* public static Mesa getMesa(class<Mesa>,BsonDocument bso )throws Exception
    {
    	Mesa result =new Mesa();
    	Iterator<String>nombreDeLosCampos= bso.keySet().iterator();
    	mesa.setId(bso.get("_id"));
    	return result;
    }*/
	private static void set(Field campo, Object result, BsonValue valorDelBson) throws Exception {
		// TODO Auto-generated method stub
		if(valorDelBson.isString())
		{
			campo.set(result, valorDelBson.asString().getValue());//result.campo()
			return;
		}
		if(valorDelBson.isDouble())
		{
			campo.setDouble(result,valorDelBson.asDouble().getValue());
			return;
		}
	}
	
	public static void main(String[]args) {
	Plato pplato =new Plato("28","Tortilla de gambas",6);
	PlatoPedido platoPedido=new PlatoPedido(pplato,2);
	
		try {
		//	BSoneador.insert(pplato);    5
			//BSoneador.insert(platoPedido);6 .Si qito los comentarios de 5 y 6 comento la siguiente linea , el update
			BSoneador.delete(pplato,"nombre","Tortilla de pescao");
			//comprobar qe plato pedido se cambio a treinta
		}catch(Exception e) {
			e.printStackTrace();
		}
		
	/*	
		try {
			Enumeration <Object>platos = BSoneador.load(Plato.class).elements();
			while(platos.hasMoreElements()) {
				Plato plato =(Plato)platos.nextElement();
				System.out.println(plato.getId());
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
	}

	private static void delete(Object objeto,Object... nombreValores ) throws Exception {
		if(nombreValores.length==0||nombreValores.length %2!=0)
		  throw new IllegalArgumentException("Esperaba un número par de parámetros");
	
		BsonDocument nuevosValores=new BsonDocument();
		for(int i=0;i<nombreValores.length;i++) {
			nuevosValores.put(nombreValores[i].toString(),getBsonValue(nombreValores[i+1]));
			i++;
		}
		
		Class<?> clase=objeto.getClass();
		BsonDocument criterio=new BsonDocument();
		Field campoId=clase.getDeclaredField("_id");
		campoId.setAccessible(true);
		Object valorId=campoId.get(objeto);
		criterio.put("_id", getBsonValue(valorId));
		System.out.println(criterio);
		System.out.println(nuevosValores);
		MongoCollection<BsonDocument>coleccion=MongoBroker.get().get().getCollection(clase.getName());
		System.out.println(clase.getName());
		coleccion.deleteOne(criterio/*, nuevosValores*/);
		
		BSONable anotacion=clase.getAnnotation(BSONable.class);
		if(anotacion==null)
			return;
		String nombreClaseDependiente=anotacion.claseDependiente();
		Class <?> claseDependiente=Class.forName(nombreClaseDependiente);
		Vector <Field> campoDependientes=findCampos(claseDependiente,clase);//busca en la clase qe paso como parametro
		//un campo de la clase qe paso como segundo
		
		if(campoDependientes==null)
			return;
		//@BSONable(campo="_id",nombre="idPlato",OnDeleteCascade=true)
		//private Plato plato;.he recuperado esto
		for(Field campoDependiente :campoDependientes) {
			//coger la anotación ver si tiene onDeleteCascada a true
			//si la tiene ,leer valor nombre de la anotación (dará "idPláto")
			//Ir a la colección PlatoPedido y hacer delete de todos los objetos
			//cuyo idPlato sea el _id del objeto principal(parámetro objeto )
		}
	}

	private static Vector<Field> findCampos(Class<?> claseDependiente, Class<?> clase) {
		// TODO Auto-generated method stub
		Vector<Field> resultado=null;
		Field [] camposClaseDependiente=claseDependiente.getDeclaredFields();
		for(int i=0;i<camposClaseDependiente.length;i++) {
			Field campo=camposClaseDependiente[i];
			if(campo.getType()==clase&&campo.getAnnotation(BSONable.class)!=null) {
				if(resultado==null)
					resultado=new Vector<>();
				resultado.add(campo);
			}
		}
		return resultado;
	}
	
}

