package com.redis.cache.service.impl;

import org.springframework.stereotype.Service;

import java.util.Map;

import javax.annotation.PostConstruct;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import com.redis.cache.service.PersonaService;
import com.redis.cache.model.Persona;

@Service
public class PersonaServiceImpl implements PersonaService{
	
	private static final String KEY = "PERSONA";
	
	private RedisTemplate<String, Persona> redisTemplate;
	private HashOperations<String, String, Persona> hashOperations;
	
	public PersonaServiceImpl(RedisTemplate<String, Persona> redisTemplate) {
		this.redisTemplate = redisTemplate;
	}
	
	@PostConstruct
	private void init() {
		hashOperations = redisTemplate.opsForHash();
	}
	
	@Override
	public void cargaDatosEnRedis(String datosPersona) {
		try {
	      JSONObject jsonObject = new JSONObject(datosPersona);
	      System.out.println("OBJECT : " + jsonObject.toString());
	      Persona persona = new Persona();
	      String id = jsonObject.get("id").toString();
	      persona.setId(jsonObject.get("id").toString());
	      persona.setName(jsonObject.get("name").toString());
	      persona.setStatus(jsonObject.get("status").toString());
	      persona.setGender(jsonObject.get("gender").toString());
	      persona.setImage(jsonObject.get("image").toString());
	      this.hashOperations.put(KEY, id, persona);
	      this.hashOperations.put(KEY.concat(id), id, persona);
	    } catch (JSONException err) {
	      System.out.println("Exception : " + err.toString());
	    }
	}

	@Override
	public Map<String, Persona> obtieneDescargados() {
		return this.hashOperations.entries(KEY);
	}
	
	@Override
	public Map<String, Persona> obtienePersona(String id) {
		return this.hashOperations.entries(KEY.concat(id));
	}

}
