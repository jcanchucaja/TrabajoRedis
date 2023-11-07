package com.redis.cache.controller;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
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

@RestController
@RequestMapping("/persona")
public class RickController {

	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	private StringRedisTemplate redisTemplate; 
	
	private final String BASE_URL = "https://rickandmortyapi.com/api/character/";
	
	@GetMapping("/{id}")
	public ResponseEntity<?> get(@PathVariable("id") Integer id) {
		try {
			ValueOperations<String, String> valueOp = redisTemplate.opsForValue();
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			String idString = id.toString();
			String claveRedis = getKey(idString);
			String dataRedis = valueOp.get(claveRedis);
			if (dataRedis != null && !dataRedis.isEmpty()) {
				return new ResponseEntity<String>(dataRedis, headers, HttpStatus.OK);
			} else {
				ResponseEntity<String> response = restTemplate.exchange(BASE_URL.concat(idString), HttpMethod.GET, null, String.class);
				String respuesta = response.getBody();
				if (response.getStatusCodeValue() == 200) {
					valueOp.set(claveRedis, respuesta, Duration.ofHours(1));
				}
				return new ResponseEntity<String>(respuesta, headers, HttpStatus.OK);
			}
		} catch (Exception e) {
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	private String getKey(String id) {
		return "PERSONA-".concat(id);
	}
}
