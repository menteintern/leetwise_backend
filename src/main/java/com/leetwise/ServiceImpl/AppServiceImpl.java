package com.leetwise.ServiceImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.leetwise.ServiceHelper.AppServiceHelper;
import com.leetwise.model.CodeExecutionRequest;
import com.leetwise.model.TestCase;
import com.leetwise.service.AppService;

import okhttp3.OkHttpClient;

@Service
public class AppServiceImpl implements AppService {
    
    private final RestTemplate restTemplate;
    
    @Value("${judge0.api.url}")
    private String judge0ApiUrl;
    
    @Value("${judge0.api.key}")
    private String judge0ApiKey;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    AppServiceHelper appServiceHelper;
    
    public AppServiceImpl(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }
    
    @Override
    public ResponseEntity<?> getQuestionData(String titleSlug) {
        String query = """
            query questionData($titleSlug: String!) {
                question(titleSlug: $titleSlug) {
                    questionId
                    questionFrontendId
                    title
                    titleSlug
                    content
                    difficulty
                    likes
                    dislikes
                    codeSnippets {
                        lang
                        langSlug
                        code
                    }
                    topicTags {
                        name
                        id
                        slug
                    }
                    hints
                    solution {
                        id
                        canSeeDetail
                        paidOnly
                    }
                    sampleTestCase
                }
            }
        """;
        
        Map<String, Object> graphQLRequest = new HashMap<>();
        graphQLRequest.put("query", query);
        graphQLRequest.put("variables", Map.of("titleSlug", titleSlug));
        
        return executeLeetCodeQuery(graphQLRequest);
    }
    
    @Override
    public ResponseEntity<?> getUserProfile(String username) {
        String query = """
            query userPublicProfile($username: String!) {
                matchedUser(username: $username) {
                    username
                    profile {
                        realName
                        userAvatar
                        birthday
                        ranking
                        reputation
                        websites
                        countryName
                        company
                        school
                        skillTags
                        aboutMe
                        starRating
                    }
                    submitStats {
                        totalSubmissionNum {
                            difficulty
                            count
                            submissions
                        }
                        acSubmissionNum {
                            difficulty
                            count
                            submissions
                        }
                    }
                    badges {
                        id
                        displayName
                        icon
                        creationDate
                    }
                    upcomingBadges {
                        name
                        icon
                    }
                    activeBadge {
                        id
                        displayName
                        icon
                    }
                }
                userContestRanking(username: $username) {
                    attendedContestsCount
                    rating
                    globalRanking
                    totalParticipants
                    topPercentage
                }
                recentAcSubmissionList(username: $username, limit: 20) {
                    title
                    titleSlug
                    timestamp
                    statusDisplay
                    lang
                }
            }
        """;
        
        Map<String, Object> graphQLRequest = new HashMap<>();
        graphQLRequest.put("query", query);
        graphQLRequest.put("variables", Map.of("username", username));
        
        return executeLeetCodeQuery(graphQLRequest);
    }
    
    private ResponseEntity<?> executeLeetCodeQuery(Map<String, Object> graphQLRequest) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(graphQLRequest, headers);
        
        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                "https://leetcode.com/graphql",
                HttpMethod.POST,
                entity,
                Map.class
            );
            
            return ResponseEntity.ok(response.getBody());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", e.getMessage()));
        }
    }
        
    @Override
    public ResponseEntity<?> executeCode(CodeExecutionRequest request) {
        try {
            
            List<Map<String, Object>> results = new ArrayList<>();
            
            String wrappedCode = appServiceHelper.wrapCodeForExecution(request.getCode(), request.getLanguage(), request.getTitleSlug());
            int languageId = appServiceHelper.getLanguageId(request.getLanguage());
            
            for (TestCase testCase : request.getTestCases()) {
                Map<String, Object> submission = new HashMap<>();
                submission.put("source_code", wrappedCode);
                submission.put("language_id", languageId);
                submission.put("stdin", testCase.getInput());
                submission.put("expected_output", testCase.getExpectedOutput());
                submission.put("cpu_time_limit", "2");
                submission.put("memory_limit", "128000");
                
                String submissionResponse = submitToJudge0(submission);
                String token = appServiceHelper.extractToken(submissionResponse);
                
                Map<String, Object> result = pollForResult(token);
                results.add(result);
            }
            
            return ResponseEntity.ok(Map.of("results", results));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", e.getMessage()));
        }
    }
    
    
    
    private String submitToJudge0(Map<String, Object> submission) throws Exception {
        try {
            
            OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();
            
            okhttp3.MediaType mediaType = okhttp3.MediaType.parse("application/json; charset=utf-8");
            String jsonBody = objectMapper.writeValueAsString(submission);
            okhttp3.RequestBody body = okhttp3.RequestBody.create(jsonBody, mediaType);
            
            okhttp3.Request request = new okhttp3.Request.Builder()
                .url(judge0ApiUrl + "/submissions?base64_encoded=false&wait=false&fields=*")
                .post(body)
                .addHeader("X-RapidAPI-Key", judge0ApiKey)
                .addHeader("X-RapidAPI-Host", "judge0-ce.p.rapidapi.com")
                .addHeader("Content-Type", "application/json")
                .build();
            
            try (okhttp3.Response response = client.newCall(request).execute()) {
                String responseBody = response.body().string();
                System.out.println("Response code: " + response.code());
                System.out.println("Response body: " + responseBody);
                
                if (!response.isSuccessful()) {
                    throw new RuntimeException("Failed to submit code: " + responseBody);
                }
                
                return responseBody;
            }
        } catch (Exception e) {
            System.out.println("Exception in submitToJudge0: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> pollForResult(String token) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-RapidAPI-Key", judge0ApiKey);
        headers.set("X-RapidAPI-Host", "judge0-ce.p.rapidapi.com");
        
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        
        int maxAttempts = 30;
        int attempt = 0;
        
        while (attempt < maxAttempts) {
            try {
                if (attempt < 5) {
                    Thread.sleep(1000);
                } else {
                    Thread.sleep(2000);
                }
                
                ResponseEntity<String> response = restTemplate.exchange(
                    judge0ApiUrl + "/submissions/" + token + "?base64_encoded=true&fields=*",
                    HttpMethod.GET,
                    entity,
                    String.class
                );
                
                if (response.getStatusCode().is2xxSuccessful()) {
                    Map<String, Object> result = objectMapper.readValue(response.getBody(), Map.class);
                    Map<String, Object> status = (Map<String, Object>) result.get("status");
                    
                    Integer statusId = (Integer) status.get("id");
                    
                    if (statusId != null && statusId > 2) {
                        appServiceHelper.decodeBase64Fields(result);
                        return result;
                    }
                }      
                attempt++;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Polling interrupted");
            } catch (Exception e) {
                System.out.println("Error during polling: " + e.getMessage());
                throw e;
            }
        }
        
        throw new RuntimeException("Timeout waiting for code execution result after " + maxAttempts + " attempts");
    }
 
}