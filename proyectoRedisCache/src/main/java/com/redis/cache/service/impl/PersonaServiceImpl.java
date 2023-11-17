package com.redis.cache.service.impl;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.time.Duration;

import javax.annotation.PostConstruct;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import com.redis.cache.service.PersonaService;

import com.redis.cache.model.Persona;

@Service
public class PersonaServiceImpl implements PersonaService{
	
	private static final String KEY = "PERSONAS";
	
	private static final String KEY_INDI = "PERSONA";
	
	private RedisTemplate<String, Persona> redisTemplate;
	private RedisTemplate<String, String> redisTemplateValores;
	private HashOperations<String, String, Persona> hashOperations;
	private HashOperations<String, String, String> hashOperation;
	private ListOperations<String, String> listOperations;
	
	public PersonaServiceImpl(RedisTemplate<String, Persona> redisTemplate, RedisTemplate<String, String> redisTemplateValores) {
		this.redisTemplate = redisTemplate;
		this.redisTemplateValores = redisTemplateValores;
	}
	
	@PostConstruct
	private void init() {
		hashOperations = redisTemplate.opsForHash();
		hashOperation = redisTemplateValores.opsForHash();
		listOperations = redisTemplateValores.opsForList();
	}
	
	@Override
	public void cargaDatosEnRedis(String datosPersona) {
		try {
	      JSONObject jsonObject = new JSONObject(datosPersona);
	      System.out.println("OBJECT : " + jsonObject.toString());
	      Persona persona = new Persona();
	      String id = jsonObject.get("id").toString();
	      String name = jsonObject.get("name").toString();
	      String status = jsonObject.get("status").toString();
	      String gender = jsonObject.get("gender").toString();
	      String image = jsonObject.get("image").toString();
	      persona.setId(id);
	      persona.setName(name);
	      persona.setStatus(status);
	      persona.setGender(gender);
	      persona.setImage(image);
	      Map<String, String> personaMap = new HashMap<String, String>();
	      personaMap.put("id", id);
	      personaMap.put("name", name);
	      personaMap.put("status", status);
	      personaMap.put("gender", gender);
	      personaMap.put("image", image);
	      // Graba objeto Persona
	      this.hashOperations.put(KEY, id, persona);
	      // Graba Hash de datos de la persona
	      this.hashOperation.putAll(KEY_INDI.concat(id), personaMap);
	      // Graba lista con datos de la persona
	      this.listOperations.rightPushAll("List".concat(KEY_INDI).concat(id), id, name, status, gender, image);
	      this.redisTemplateValores.expire("List".concat(KEY_INDI).concat(id), Duration.ofHours(1));
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

	public JSONObject convertirAJSON(String datosPersona) {
		JSONObject jsonObject = new JSONObject(datosPersona);
		return jsonObject;
	}
	
	
	public void consultarData(JSONObject jsonObject) {
		try {
	      System.out.println("OBJECT : " + jsonObject.toString());
	      Persona persona = new Persona();
	      String id = jsonObject.get("id").toString();
	      String name = jsonObject.get("name").toString();
	      String status = jsonObject.get("status").toString();
	      String gender = jsonObject.get("gender").toString();
	      String image = jsonObject.get("image").toString();
	      persona.setId(jsonObject.get("id").toString());
	      persona.setName(jsonObject.get("name").toString());
	      persona.setStatus(jsonObject.get("status").toString());
	      persona.setGender(jsonObject.get("gender").toString());
	      persona.setImage(jsonObject.get("image").toString());
	      this.hashOperation.put(KEY_INDI.concat(id), "ID", persona.getId());
	      this.hashOperation.put(KEY_INDI.concat(id), "NOMBRE", persona.getName());
	      this.hashOperation.put(KEY_INDI.concat(id), "ESTADO", persona.getStatus());
	      this.hashOperation.put(KEY_INDI.concat(id), "GENERO", persona.getGender());
	      this.hashOperation.put(KEY_INDI.concat(id), "FOTO", persona.getImage());
	    } catch (JSONException err) {
	      System.out.println("Exception : " + err.toString());
	    }
	}
	
	public void insertarEnCache(JSONObject jsonObject) {
		try {
	       System.out.println("OBJECT : " + jsonObject.toString());
	      Persona persona = new Persona();
	      String id = jsonObject.get("id").toString();
	      persona.setId(id);
	      persona.setName(jsonObject.get("name").toString());
	      persona.setStatus(jsonObject.get("status").toString());
	      persona.setGender(jsonObject.get("gender").toString());
	      persona.setImage(jsonObject.get("image").toString());
	      this.hashOperations.put(KEY, persona.getId(), persona);

	    } catch (JSONException err) {
	      System.out.println("Exception : " + err.toString());
	    }
	}
	
	public void eliminarEnCache(Persona persona) {
		try {
 	      this.hashOperations.delete(KEY, persona.getId());

	    } catch (JSONException err) {
	      System.out.println("Exception : " + err.toString());
	    }
	}
	
	public void actualizarEnCache(Persona persona) {
		try {
	
	      persona.setName("Nombre Actualizado");
	      persona.setStatus("Status Actualizado");
	      persona.setGender("Genero Actualizado");
	      persona.setImage("Imagen Actualizado");
	      this.hashOperations.put(KEY, persona.getId(), persona);

	    } catch (JSONException err) {
	      System.out.println("Exception : " + err.toString());
	    }
	}
	
	
	   public List<Persona> findAll(){
	        return hashOperations.values(KEY);
	    }
	 
	    public Persona findById(Persona persona) {
	        return (Persona) hashOperations.get(KEY, persona.getId());
	    }
	
}
