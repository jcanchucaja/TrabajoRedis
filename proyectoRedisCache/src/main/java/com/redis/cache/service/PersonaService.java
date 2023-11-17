package com.redis.cache.service;

import java.util.Map;

import com.redis.cache.model.Persona;

public interface PersonaService {

	public String getPersona (String clavePersona);
	
	public void setPersona(String datosPersona);
	
	public Map<String, Persona> obtieneDescargados();
	
	public Map<String, Persona> obtienePersona(String id);
	
	public void actualizarEstado(String id, String estado);
	
	public void eliminaPersona(String id);

}
