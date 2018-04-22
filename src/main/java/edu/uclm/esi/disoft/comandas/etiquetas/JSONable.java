package edu.uclm.esi.disoft.comandas.etiquetas;


import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import static java.lang.annotation.ElementType.FIELD;
import java.lang.annotation.RetentionPolicy;


@Target(FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface JSONable {

	String nombre()default"";

	String campo()default"";
	

}
