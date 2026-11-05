package io.github.HarryCodeProg.TrucoSurvivors;
import io.github.HarryCodeProg.TrucoSurvivors.Gestores.GestorAnimacionResolucion;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.ResolucionPuntaje;
import io.github.HarryCodeProg.TrucoSurvivors.Modelo.ConfiguracionJuego;
import com.badlogic.gdx.*;
import java.lang.reflect.Proxy;
import java.util.*;

public class AnimacionRegression {
 static int checks;
 static void check(boolean ok,String msg){checks++;if(!ok)throw new AssertionError(msg);}
 static void eq(double a,double b,String msg){check(Math.abs(a-b)<0.00001,msg+": "+a+" != "+b);}
 static ResolucionPuntaje resolucion(){
  ResolucionPuntaje r=new ResolucionPuntaje(0,1);
  r.registrarPuntajesPrevios(100,50);
  r.sumarChips(10,"Carta");r.sumarMult(2,"Joker",null);
  r.sumarChips(10,"Reactivacion");r.multiplicarMult(2,"Final",null);
  return r;
 }
 public static void main(String[] args){
  for(int velocidad:new int[]{1,2,4}){
   GestorAnimacionResolucion g=new GestorAnimacionResolucion();g.setVelocidad(velocidad);
   int[] pasos={0},finales={0};
   g.iniciar(resolucion(),p->pasos[0]++,()->{finales[0]++;eq(g.getPuntosJugadorVisibles(220),220,"Total disponible en callback");});
   eq(g.getPuntosJugadorVisibles(220),100,"Total anterior al iniciar");
   eq(g.getPuntosRivalVisibles(80),50,"Rival anterior al iniciar");
   g.update(2.5f/velocidad);
   eq(pasos[0],5,"Todos los pasos en orden temporal");check(g.isActiva(),"Ultimo paso sigue visible");
   eq(g.getPuntosJugadorVisibles(220),100,"No revelar mientras se muestra ultimo paso");
   g.update(0.51f/velocidad);check(!g.isActiva(),"Finaliza a velocidad elegida");
   eq(g.getPuntosJugadorVisibles(220),220,"Revela total final");
   eq(g.getPuntosRivalVisibles(80),80,"Revela rival final");
   g.update(100);eq(finales[0],1,"Callback una sola vez");
  }
  GestorAnimacionResolucion g=new GestorAnimacionResolucion();List<String> orden=new ArrayList<>();
  g.iniciar(resolucion(),p->orden.add(p.origen),null);g.setVelocidad(4);g.update(1);
  check(orden.equals(Arrays.asList("Base","Carta","Joker","Reactivacion","Final")),"Frame largo no pierde ni reordena pasos");
  g.iniciar(resolucion(),null,null);g.setVelocidad(1);g.update(0.3f);g.setVelocidad(2);g.update(0.16f);
  check("Carta".equals(g.getPasoActual().origen),"Cambio de velocidad conserva avance parcial");
  g.iniciar(new ResolucionPuntaje(0,1),null,null);eq(g.getPuntosJugadorVisibles(999),999,"Compatibilidad sin snapshot");
  Map<String,Object> valores=new HashMap<>();
  Preferences prefs=(Preferences)Proxy.newProxyInstance(Preferences.class.getClassLoader(),new Class[]{Preferences.class},(p,m,a)->{
   if(m.getName().startsWith("put")&&a.length==2){valores.put((String)a[0],a[1]);return p;}
   if(m.getName().startsWith("get")&&a.length==2)return valores.getOrDefault(a[0],a[1]);
   return null;
  });
  Gdx.app=(Application)Proxy.newProxyInstance(Application.class.getClassLoader(),new Class[]{Application.class},(p,m,a)->m.getName().equals("getPreferences")?prefs:null);
  ConfiguracionJuego c=new ConfiguracionJuego();c.cargar();eq(c.getVelocidadActivaciones(),1,"Preferencia ausente usa x1");
  c.setVelocidadActivaciones(4);c.guardar();c=new ConfiguracionJuego();c.cargar();eq(c.getVelocidadActivaciones(),4,"Velocidad persistida");
  c.setVelocidadActivaciones(3);eq(c.getVelocidadActivaciones(),1,"Valor invalido normalizado");
  System.out.println("PASS: "+checks+" comprobaciones de animacion, marcador y preferencias.");
 }
}
