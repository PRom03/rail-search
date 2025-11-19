package org.example.railsearch;

import lombok.Data;

@Data
public class OrderResponse {
    private String redirectUri;
    private String orderId;
    private Status status;

    @Data
    public static class Status {
        private String statusCode;
    }
}

