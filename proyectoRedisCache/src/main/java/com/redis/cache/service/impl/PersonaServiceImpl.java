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
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.ZSetOperations;

import com.redis.cache.service.PersonaService;

import com.redis.cache.model.Persona;

@Service
public class PersonaServiceImpl implements PersonaService{
	
	private static final String KEY_TODOS = "PERSONAS";
	
	private static final String KEY_INDI = "PERSONA";
	
	private RedisTemplate<String, Persona> redisTemplate;
	private RedisTemplate<String, String> redisTemplateValores;
	
	private HashOperations<String, String, Persona> hashOperations;
	private HashOperations<String, String, String> hashOperation;
	private ListOperations<String, String> listOperations;
	private SetOperations<String, String> setOperations;
	private ZSetOperations<String, String> zSetOperations;
	private ValueOperations<String, String> valueOperations;
	
	public PersonaServiceImpl(RedisTemplate<String, Persona> redisTemplate, RedisTemplate<String, String> redisTemplateValores) {
		this.redisTemplate = redisTemplate;
		this.redisTemplateValores = redisTemplateValores;
	}
	
	@PostConstruct
	private void init() {
		hashOperations = redisTemplate.opsForHash();
		hashOperation = redisTemplateValores.opsForHash();
		listOperations = redisTemplateValores.opsForList();
		setOperations = redisTemplateValores.opsForSet();
		zSetOperations = redisTemplateValores.opsForZSet();
		valueOperations = redisTemplateValores.opsForValue();
	}
	
	@Override
	public String getPersona (String id) {
		return this.valueOperations.get("String".concat(KEY_INDI).concat(id));
	}
	
	@Override
	public void setPersona(String datosPersona) {
		try {
	      JSONObject jsonObject = new JSONObject(datosPersona);
	      Duration tiempoDuracion = Duration.ofMinutes((long)10); // Se define el tiempo de duracion
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
	      // Graba todos los datos de la persona como String del JSON de respuesta
	      this.valueOperations.set("String".concat(KEY_INDI).concat(id), datosPersona, tiempoDuracion);
	      // Graba objeto Persona en un hash que tiene todas las personas consultadas.
	      this.hashOperations.put(KEY_TODOS, id, persona);
	      this.redisTemplate.expire(KEY_TODOS, tiempoDuracion); // Se le coloca el tiempo de expiracion de una hora
	      // Graba en Hash como Persona solo los datos de la persona consultada
	      this.hashOperations.put("HashPersona".concat(KEY_INDI).concat(id), id, persona);
	      this.redisTemplate.expire("HashPersona".concat(KEY_INDI).concat(id), tiempoDuracion); // Se le coloca el tiempo de expiracion de una hora
	      // Graba en Hash como Map solo los datos de la persona consultada
	      this.hashOperation.putAll("HashMap".concat(KEY_INDI).concat(id), personaMap);
	      this.redisTemplateValores.expire("HashMap".concat(KEY_INDI).concat(id), tiempoDuracion); // Se le coloca el tiempo de expiracion de una hora
	      // Graba en lista solo los datos de la persona consultada
	      this.listOperations.rightPushAll("List".concat(KEY_INDI).concat(id), id, name, status, gender, image);
	      this.redisTemplateValores.expire("List".concat(KEY_INDI).concat(id), tiempoDuracion); // Se le coloca el tiempo de expiracion de una hora
	      // Graba en set solo los datos de la persona consultada
	      this.setOperations.add("Set".concat(KEY_INDI).concat(id), id, name, status, gender, image);
	      this.redisTemplateValores.expire("Set".concat(KEY_INDI).concat(id), tiempoDuracion); // Se le coloca el tiempo de expiracion de una hora
	      // Graba en SortSet la persona consultada en orden de consulta
	      Long orden = this.zSetOperations.size("SortSet".concat(KEY_TODOS)) + 1;
	      this.zSetOperations.add("SortSet".concat(KEY_TODOS), id.concat("-").concat(name), orden);
	      this.redisTemplateValores.expire("SortSet".concat(KEY_TODOS), tiempoDuracion); // Se le coloca el tiempo de expiracion de una hora
	    } catch (JSONException err) {
	      System.out.println("Exception : " + err.toString());
	    }
	}

	@Override
	public Map<String, Persona> obtieneDescargados() {
		return this.hashOperations.entries(KEY_TODOS);
	}
	
	@Override
	public Map<String, Persona> obtienePersona(String id) {
		return this.hashOperations.entries("HashPersona".concat(KEY_INDI).concat(id));
	}
	
	public void eliminarEnCache(Persona persona) {
		try {
 	      this.hashOperations.delete(KEY_TODOS, persona.getId());

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
	      this.hashOperations.put(KEY_TODOS, persona.getId(), persona);

	    } catch (JSONException err) {
	      System.out.println("Exception : " + err.toString());
	    }
	}
	
	
	   public List<Persona> findAll(){
	        return hashOperations.values(KEY_TODOS);
	    }
	 
	    public Persona findById(Persona persona) {
	        return (Persona) hashOperations.get(KEY_TODOS, persona.getId());
	    }
	
}
