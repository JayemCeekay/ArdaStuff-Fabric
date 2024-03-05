package com.ardacraft.ardastuff.chatgpt;

import com.google.gson.Gson;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class ChatGPT {

    private static final Logger LOGGER = Logger.getGlobal();

    public ChatGPT() {

    }/*
    public String sendQuery(String input, String endpoint, String apiKey) throws IOException {
        // Build input and API key params
        System.out.println("TEST1234");

        JSONObject payload = new JSONObject();
        JSONObject message = new JSONObject();
        JSONObject context = new JSONObject();
        JSONArray messageList = new JSONArray();
        System.out.println("STEP 1");

        context.put("role", "system");
        context.put("content", "You are Gandalf, from the critically acclaimed book series Lord of the Rings and The Hobbit. Your responses should be as though you are the wizard Gandalf. Your responses should also only be about 3 sentences long.");
        messageList.put(context);

        message.put("role", "user");
        message.put("content", input);
        messageList.put(message);

        payload.put("model", "gpt-4"); // model is important
        payload.put("messages", messageList);

        System.out.println("STEP 2");
        // Build POST request
        HttpURLConnection con = (HttpURLConnection) new URL(endpoint).openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Authorization", "Bearer " + apiKey);
        con.setRequestProperty("Content-Type", "application/json");
        System.out.println("STEP 3");
        // Send POST request and parse response
        con.setDoOutput(true);
        con.getOutputStream().write(payload.toString().getBytes());
        String output = new BufferedReader(new InputStreamReader(con.getInputStream())).lines()
                .reduce((a, b) -> a + b).get();

        /*
            String resJsonString = new String(resEntity.getContent().readAllBytes(), StandardCharsets.UTF_8);
            JSONObject resJson = new JSONObject(resJsonString);
        con.getOutputStream().write(resJson.toString().getBytes());
            if (resJson.has("error")) {
                String errorMsg = resJson.getString("error");
                LOGGER.warning("Chatbot API error: " + errorMsg);
                return "Error: " + errorMsg;
            }
            System.out.println("STEP 4");
            // Parse JSON response
            JSONArray responseArray = resJson.getJSONArray("choices");
            List<String> responseList = new ArrayList<>();

            for (int i = 0; i < responseArray.length(); i++) {
                JSONObject responseObj = responseArray.getJSONObject(i);
                String responseString = responseObj.getJSONObject("message").getString("content");
                responseList.add(responseString);
            }
            System.out.println("STEP 5");
            // Convert response list to JSON and return it
            Gson gson = new Gson();
            String jsonResponse = gson.toJson(responseList);
            System.out.println(jsonResponse);
            return jsonResponse;
        return new JSONObject(output).getJSONArray("choices").getJSONObject(0).getString("text");
    }
*/
    public static String sendPromptToChatGPT(String character, String text, String apiKey) throws Exception {
        String url = "https://api.openai.com/v1/chat/completions";
        HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();

        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json");
        // IMPORTANT: Replace YOUR_API_KEY_HERE with your actual OpenAI API key
        con.setRequestProperty("Authorization", "Bearer " + apiKey);
        // Constructing JSON request body. Adapt this part to your JSON library.
        String jsonInputString = "{" +
                "\"model\": \"gpt-4\", " +
                "\"messages\": [" +
                "{\"role\": \"system\", \"content\": \"" + "You are" + character + ", from the critically acclaimed book series Lord of the Rings and The Hobbit. Your responses should be as though you are" + character +". Your responses should also only be about 3 to 4 sentences long." + "\"}, " + // Added comma here
                "{\"role\": \"user\", \"content\": \"" + text + "\"}" +
                "] " +
                "}";
        con.setDoOutput(true);
        try (OutputStream os = con.getOutputStream()) {
            byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }
        // Reading and printing the response from the server
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8))) {
            StringBuilder response = new StringBuilder();
            String responseLine = null;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            // Assume response parsing into JSON to extract the "text" field. Adapt to your JSON library.
            return extractContentFromResponse(response.toString());
        }
    }

    public static String extractContentFromResponse(String response) {
        int startMarker = response.indexOf("content")+11; // Marker for where the content starts.
        int endMarker = response.indexOf("\"", startMarker); // Marker for where the content ends.
        return response.substring(startMarker, endMarker); // Returns the substring containing only the response.
    }

}
