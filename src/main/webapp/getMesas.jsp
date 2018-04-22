<%@ page language="java" contentType="application/json; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="org.json.*, edu.uclm.esi.disoft.comandas.dominio.Manager" %>

<%
	response.addHeader("Access-Control-Allow-Origin", "*");//esto es importante para qe se me acepten envios desde
	//aqui al enviar a otro servidor desde el cliente
	JSONArray mesas=Manager.get().getMesas();
%>

<%= mesas %>