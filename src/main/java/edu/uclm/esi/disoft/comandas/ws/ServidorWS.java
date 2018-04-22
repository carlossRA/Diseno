package edu.uclm.esi.disoft.comandas.ws;

import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.json.JSONArray;
import org.json.JSONObject;

@ServerEndpoint(value="/ServidorWS")//CON ESTO ESTOY PREPARADO PARA HACER COMUNICACIÓN CON QIEN QIERA
public class ServidorWS {
	
	private static ConcurrentHashMap<String,Session> sessions=new ConcurrentHashMap();
	
	@OnOpen //aqui llega un cliente
	public void onOpen(Session session) {
		//System.out.println(session.getId());
		Map <String,List<String>> mapa=session.getRequestParameterMap();
		List <String> parametros=mapa.get("user");
		sessions.put(parametros.get(0),session);
		System.out.println(parametros.get(0));
	}
	@OnMessage//este metodo se ejecuta cuando recibe un mensaje , el objeto llega en JSON , abrá qe convertir
	//el texto a JSON
	public void onMessage(Session session,String mensaje) {
		JSONObject jso=new JSONObject(mensaje);
		String type=jso.getString("type");
		switch(type) {
		case "PlatoPreparado":
			
			break;
		case "MensajeAUsuario":
			String destinatario=jso.getString("destinatario");
			String texto=jso.getString("texto");
			enviar(destinatario,texto);
			
			break;
		case "MensajeATodos":
			texto=jso.getString("texto");
			enviarATodos(texto);
			break;
		}
	}
	private void enviarATodos(String texto) {
		// TODO Auto-generated method stub
		
	}
	private void enviar(String destinatario, String texto) {
		Session sesionDestinatario=sessions.get(destinatario);
		if(sesionDestinatario==null)
		{
			sessions.remove(destinatario);
			return;
		}
		JSONObject mensaje=new JSONObject();
		mensaje.put("type","MensajeIndividual");
		mensaje.put("texto",texto);
		sesionDestinatario.getAsyncRemote().sendText(mensaje.toString());
	}
	public static void solicitarPlato(JSONArray platos) {
	JSONObject mensaje =new JSONObject();
	mensaje.put("type","solicitudDePlatos");
	mensaje.put("platos",platos);
	Enumeration <Session >sesiones=sessions.elements();
	while(sesiones.hasMoreElements()) {
      Session sesion=sesiones.nextElement();
      sesion.getAsyncRemote().sendText(mensaje.toString());
	}
}
}
