package org.example.pdnight.domain.auth.application.authUseCase;

import org.springframework.util.MultiValueMap;

import java.util.Map;

public interface HttpClientPort {

    Map<String, Object> postForm(String url, MultiValueMap<String, String> form);

    Map<String, Object> get(String url, String accessToken);

}
