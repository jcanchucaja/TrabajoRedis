package com.redis.cache.service;

import java.util.List;
import java.util.Map;

import com.redis.cache.model.Persona;

public interface PersonaService {

	public String getPersona (String clavePersona);
	
	public void setPersona(String datosPersona);
	
	public Map<String, Persona> obtieneDescargados();
	
	public Map<String, Persona> obtienePersona(String id);
	
	public void eliminarEnCache(Persona persona);
	
	public void actualizarEnCache(Persona persona);
	
	public List<Persona> findAll();
	
	public Persona findById(Persona persona);

}
