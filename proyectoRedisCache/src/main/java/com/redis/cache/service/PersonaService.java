package com.redis.cache.service;

import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import com.redis.cache.model.Persona;

public interface PersonaService {

	public void cargaDatosEnRedis(String datosPersona);
	
	public Map<String, Persona> obtieneDescargados();
	
	public Map<String, Persona> obtienePersona(String id);

	public JSONObject cargaDatosRedis(String datosPersona);
	
	public void consultarData(JSONObject jsonObject);
	
	public void insertarEnCache(JSONObject jsonObject);
	
	public void eliminarEnCache(Persona persona);
	
	public void actualizarEnCache(Persona persona);
	
	public List<Persona> findAll();
	
	public Persona findById(Persona persona);

}
