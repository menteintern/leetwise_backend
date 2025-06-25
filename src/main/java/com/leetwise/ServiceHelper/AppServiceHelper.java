package com.leetwise.ServiceHelper;

import java.util.Base64;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class AppServiceHelper {
	
	@Autowired
    private ObjectMapper objectMapper;
	
	public int getLanguageId(String language) {
        Map<String, Integer> languageMap = Map.of(
            "cpp", 54,
            "java", 62,
            "python3", 71,
            "javascript", 63,
            "c", 50,
            "python", 71,
            "go", 60,
            "rust", 73
        );
        return languageMap.getOrDefault(language, 71);
    }
	
	@SuppressWarnings("unchecked")
    public String extractToken(String submissionResponse) throws Exception {
        try {
            
			Map<String, Object> responseMap = objectMapper.readValue(submissionResponse, Map.class);
            String token = (String) responseMap.get("token");
            
            if (token == null || token.isEmpty()) {
                throw new RuntimeException("No token received from Judge0");
            }
            
            return token;
        } catch (Exception e) {
            throw new RuntimeException("Failed to extract token from response: " + e.getMessage());
        }
    }
	
	public String wrapCodeForExecution(String code, String language, String titleSlug) {
        if (language.equals("cpp")) {
            return """
                #include <bits/stdc++.h>
                #include <algorithm>
                using namespace std;
                
                """ + code + """
                
                int main() {
                    Solution solution;
                    
                    string input;
                    cin >> input;
                    
                    bool result = solution.isValid(input);
                    cout << (result ? "true" : "false") << endl;
                    
                    return 0;
                }
                """;
        } else if (language.equals("java")) {
            return """
                import java.util.*;
                import java.io.*;
                
                """ + code + """
                
                class Main {
                    public static void main(String[] args) {
                        Scanner scanner = new Scanner(System.in);
                        Solution solution = new Solution();
                        
                        String input = scanner.nextLine();
                        boolean result = solution.isValid(input);
                        System.out.println(result);
                    }
                }
                """;
        } else if (language.equals("python3") || language.equals("python")) {
            return code + """
                
                if __name__ == "__main__":
                    import sys
                    input_str = input().strip()
                    
                    solution = Solution()
                    result = solution.isValid(input_str)
                    print("true" if result else "false")
                """;
        }
        
        return code;
    }
	
	public void decodeBase64Fields(Map<String, Object> result) {
        try {
            if (result.containsKey("stdout") && result.get("stdout") != null) {
                String encodedStdout = (String) result.get("stdout");
                String decodedStdout = new String(Base64.getDecoder().decode(encodedStdout));
                result.put("stdout", decodedStdout);
            }
            
            if (result.containsKey("stderr") && result.get("stderr") != null) {
                String encodedStderr = (String) result.get("stderr");
                String decodedStderr = new String(Base64.getDecoder().decode(encodedStderr));
                result.put("stderr", decodedStderr);
            }
            
            if (result.containsKey("compile_output") && result.get("compile_output") != null) {
                String encodedCompileOutput = (String) result.get("compile_output");
                String decodedCompileOutput = new String(Base64.getDecoder().decode(encodedCompileOutput));
                result.put("compile_output", decodedCompileOutput);
            }
            
            if (result.containsKey("message") && result.get("message") != null) {
                String encodedMessage = (String) result.get("message");
                String decodedMessage = new String(Base64.getDecoder().decode(encodedMessage));
                result.put("message", decodedMessage);
            }
        } catch (Exception e) {
            System.out.println("Error decoding base64 fields: " + e.getMessage());
        }
    }
}
