package com.akshat.college_project.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Service
public class OneDriveStorageService {

    private final WebClient webClient;

    @Value("${azure.client.id}")
    private String clientId;

    @Value("${azure.client.secret}")
    private String clientSecret;

    @Value("${azure.tenant.id}")
    private String tenantId;

    @Value("${azure.graph.token-url}")
    private String tokenUrl;

    @Value("${azure.graph.scope}")
    private String scope;

    @Value("${onedrive.drive.id}")
    private String driveId;

    @Value("${onedrive.root.folder}")
    private String rootFolder;

    // Token caching
    private String cachedToken;
    private long tokenExpiryTime;

    public OneDriveStorageService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    private synchronized String getAccessToken() {
        if (cachedToken != null && System.currentTimeMillis() < tokenExpiryTime) {
            return cachedToken;
        }

        Map<String, String> formData = new ConcurrentHashMap<>();
        formData.put("client_id", clientId);
        formData.put("scope", scope);
        formData.put("client_secret", clientSecret);
        formData.put("grant_type", "client_credentials");

        TokenResponse response = webClient.post()
                .uri(tokenUrl)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData("client_id", clientId)
                        .with("scope", scope)
                        .with("client_secret", clientSecret)
                        .with("grant_type", "client_credentials"))
                .retrieve()
                .bodyToMono(TokenResponse.class)
                .block();

        if (response != null && response.accessToken != null) {
            this.cachedToken = response.accessToken;
            // Buffer expiry by 5 minutes
            this.tokenExpiryTime = System.currentTimeMillis() + ((response.expiresIn - 300) * 1000L);
        } else {
            throw new RuntimeException("Failed to obtain Microsoft Graph Access Token");
        }
        return this.cachedToken;
    }

    /**
     * Uploads a file to Microsoft Graph API OneDrive.
     * Path pattern: /{rootFolder}/{projectId}/{stage}/{filename}
     */
    public String uploadFile(MultipartFile file, String projectId, String stage) throws IOException {
        String token = getAccessToken();
        
        String filename = file.getOriginalFilename();
        if (filename == null) filename = "upload.bin";
        // Sanitize for URL
        filename = filename.replace(" ", "_");

        // Construct Path: moto-service-hub/PROJECT_ID/STAGE/filename
        String graphUploadUrl = String.format("https://graph.microsoft.com/v1.0/drives/%s/root:/%s/%s/%s/%s:/content",
                driveId, rootFolder, projectId, stage, filename);

        // WebClient handles large streams naturally, but block for simple return
        OneDriveUploadResponse response = webClient.put()
                .uri(graphUploadUrl)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(BodyInserters.fromResource(file.getResource()))
                .retrieve()
                .bodyToMono(OneDriveUploadResponse.class)
                .block();

        if (response != null && response.webUrl != null) {
            return response.webUrl;
        }
        throw new RuntimeException("Failed to upload file to OneDrive");
    }

    // DTOs for Graph API Mapping
    private static class TokenResponse {
        @JsonProperty("access_token")
        public String accessToken;
        @JsonProperty("expires_in")
        public int expiresIn;
    }

    private static class OneDriveUploadResponse {
        @JsonProperty("@microsoft.graph.downloadUrl")
        public String downloadUrl;
        @JsonProperty("webUrl")
        public String webUrl;
        @JsonProperty("id")
        public String id;
    }
}
