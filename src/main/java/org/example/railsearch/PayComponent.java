package org.example.railsearch;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.example.railsearch.Services.TicketService;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PayComponent {

    private final RestTemplate restTemplate;
    @PersistenceContext
    private EntityManager entityManager;
//    @Value("${payu.clientId}")
    private String clientId;
//    @Value("${payu.clientSecret}")
    private String clientSecret;
//    @Value("${payu.posId}")
    private String posId;
//    @Value("${payu.apiUrl}")
    private String apiUrl;

    private String fetchToken() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "client_credentials");
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(apiUrl + "/pl/standard/user/oauth/authorize", request, Map.class);
        return (String) response.getBody().get("access_token");
    }

    public OrderResponse createOrder(TicketService.TicketCreateDto dto) {
        String token = fetchToken();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);
        Long nextId = ((Number) entityManager
                .createNativeQuery("SELECT nextval('ticket_id_seq')")
                .getSingleResult()).longValue();
        Map<String, Object> payload = new HashMap<>();
        payload.put("merchantPosId", posId);
        payload.put("description", "Bilet");
        payload.put("currencyCode", "PLN");
        payload.put("totalAmount", dto.price().multiply(BigDecimal.valueOf(100)).toString());
        payload.put("extOrderId", UUID.randomUUID().toString());
        payload.put("continueUrl", "https://http://localhost:5173/tickets/my"+nextId);
        payload.put("customerIp", "127.0.0.1");

        Map<String, Object> buyer = Map.of("email", "dto.email()");
        payload.put("buyer", buyer);

        Map<String, Object> product = Map.of(
                "name", "Bilet",
                "unitPrice", dto.price().multiply(BigDecimal.valueOf(100)).toString(),
                "quantity", 1
        );
        payload.put("products", List.of(product));

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);
        ResponseEntity<OrderResponse> response = restTemplate.postForEntity(apiUrl + "/api/v2_1/orders", request, OrderResponse.class);

        return response.getBody();
    }
}

