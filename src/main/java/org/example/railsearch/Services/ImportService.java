package org.example.railsearch.Services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;


@Service
    public class ImportService {

        private final MongoTemplate mongoTemplate;
        private final RestTemplate restTemplate = new RestTemplate();

        public ImportService(MongoTemplate mongoTemplate) {
            this.mongoTemplate = mongoTemplate;
        }

        public void retrieveStations() {
            for (int i = 3; i <= 7; i++) {
                String url = "https://pdp-api.plk-sa.pl/api/v1/dictionaries/stations?page="+i;
                RestTemplate restTemplate = new RestTemplate();

                HttpHeaders headers = new HttpHeaders();
                headers.set("X-API-KEY", "gX83N1celJ-hS8vWjV3ZJFMMrLzYfkWUv8o3mJ1-A3yHJNdp6_JIUtbAC8Kpm02ZVBEusKSNa1OlWl6McbR2Jg");
                headers.setContentType(MediaType.APPLICATION_JSON);
                headers.setAccept(List.of(MediaType.APPLICATION_JSON));

                HttpEntity<Void> entity = new HttpEntity<>(headers);

                ResponseEntity<String> response = restTemplate.exchange(
                        url,
                        HttpMethod.GET,
                        entity,
                        String.class
                );

                ObjectMapper mapper = new ObjectMapper();

                try {
                    JsonNode jsonNode = mapper.readTree(response.getBody());

                    if (jsonNode.isArray()) {
                        for (JsonNode node : jsonNode) {
                            Document doc = Document.parse(node.toString());
                            mongoTemplate.insert(doc, "stations");
                        }
                    } else {
                        Document doc = Document.parse(jsonNode.toString());
                        mongoTemplate.insert(doc, "stations");
                    }

                } catch (Exception e) {
                    throw new RuntimeException(e);


                }
            }
        }
}
