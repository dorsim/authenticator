package com.cyberark.m3lclient;

import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

public class M3LClient
{
    public boolean checkHash(String hash) {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setMessageConverters(Collections.singletonList(new MappingJackson2HttpMessageConverter()));
        ResponseEntity<Result> response = restTemplate.getForEntity("http://ec2-18-188-184-139.us-east-2.compute.amazonaws.com/application/" + hash, Result.class);

        return (response.getBody().getValid());
    }
}
