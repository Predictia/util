package es.predictia.util;

public class Cronometro {

	public Cronometro(String nombre){
		this.nombre = nombre;
		this.tiempo = System.nanoTime();
	}
	
	private final String nombre;
	private long tiempo;
	
	public String getNombre() {
		return nombre;
	}
	public long getTiempo() {
		return tiempo;
	}
	public void setTiempo(long tiempo) {
		this.tiempo = tiempo;
	}

	public void reset(){
		this.tiempo = System.nanoTime();
	}

	/**
	 * @param unidades
	 * @return long
	 */
	public long getTiempoTranscurrido(Unidades unidades){
		return (long) ((System.nanoTime() - getTiempo()) / unidades.divisor);
	}
	
	public String toString(){
		String prefix = (getNombre().isEmpty()) ? "[Cronometro] " : "[Cronometro] " + getNombre() +" - ";
		return prefix + getTiempoTranscurrido(Unidades.ms) + " ms.";
	}
	
	public enum Unidades{
		ns(1d), us(1e3), ms(1e6), s(1e9);
		private Unidades(double divisor){
			this.divisor = divisor;
		}
		private final double divisor;
	}
}
