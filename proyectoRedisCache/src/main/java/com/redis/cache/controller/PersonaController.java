package com.redis.cache.controller;

import java.util.Map;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.redis.cache.model.Persona;
import com.redis.cache.service.PersonaService;

@RestController
@RequestMapping("/persona")
public class PersonaController {

	@Autowired
	private RestTemplate restTemplate;
	
	private final String BASE_URL = "https://rickandmortyapi.com/api/character/";
	
	private final PersonaService personaService;
	
	JSONObject datosEnJSON = null;
	
	public PersonaController (PersonaService personaService) {
		this.personaService = personaService;
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<?> get(@PathVariable("id") Integer id) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			String idString = id.toString();
			String dataRedis = this.personaService.getPersona(idString); // Consulta si la persona ya figura en REDIS
			if (dataRedis == null || dataRedis.isEmpty()) {
				ResponseEntity<String> response = restTemplate.exchange(BASE_URL.concat(idString), HttpMethod.GET, null, String.class);
				String respuesta = response.getBody();
				if (response.getStatusCodeValue() == 200) {
					this.personaService.setPersona(respuesta); // Registra en REDIS los datos de la persona consultada
				}
			}
			Map<String, Persona> datosRespuesta = this.personaService.obtienePersona(idString);
			return new ResponseEntity<Map<String, Persona>>(datosRespuesta, headers, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping("/descargados")
	public Map<String, Persona> obtieneDescargados() {
		return this.personaService.obtieneDescargados();
	}
	
	@GetMapping("/descargados/{id}")
	public Map<String, Persona> obtienePersona(@PathVariable("id") Integer id) {
		return this.personaService.obtienePersona(id.toString());
	}
}
