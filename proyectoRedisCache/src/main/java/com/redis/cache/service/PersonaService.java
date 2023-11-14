package com.redis.cache.service;

import java.util.Map;

import com.redis.cache.model.Persona;

public interface PersonaService {

	public void cargaDatosEnRedis(String datosPersona);
	
	public Map<String, Persona> obtieneDescargados();
	
	public Map<String, Persona> obtienePersona(String id);
}
