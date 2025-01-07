package com.programming.taha.Youtubeclone.service;
import com.programming.taha.Youtubeclone.dto.AiChatDto;
import com.programming.taha.Youtubeclone.model.Video;
import lombok.RequiredArgsConstructor;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
@RequiredArgsConstructor
public class LlamaAiService {
    @Value("${groq.api.key}")
    private String groq_API_KEY;

    private TranscriptionService transcriptionService;

    public String startChat(String userPrompt, Video video){
        String body = prepareBody(userPrompt, video);

            //prepare a http request to get the groq api response
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .uri(URI.create("https://api.groq.com/openai/v1/chat/completions"))
                .setHeader("Content-Type", "application/json")
                .setHeader("Authorization", String.format("Bearer %s", groq_API_KEY))
                .build();

        HttpClient httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .build();


        try {

            //retrieve and parse the json response from the AI
            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            JSONObject jsonObject = new JSONObject(response.body());

            String aiResponse = jsonObject
                    .getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content");

            video.appendToAiChatHistory("user", userPrompt);
            video.appendToAiChatHistory( "assistant", aiResponse);

            return aiResponse;
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    public void deleteAiChatHistory(Video video){
        video.clearAiChatHistory();
    }

    private static String prepareBody(String userPrompt, Video video) {

        String context = String.format("You are an AI assistant helping users ask questions " +
                "about the video. Your job is to answer questions strictly related to the " +
                "video's content. If a user asks a question unrelated to the video, politely " +
                "remind them to ask relevant questions. Do not mention or reference the word " +
                "'transcript' in your responses. Do not offer advice or engage in topics outside" +
                " the video's content, even if asked."
                +
                "Video transcript: transcript: %s", video.getTranscription());

        JSONArray messages = new JSONArray();
        messages.put(new JSONObject()
                .put("role", "system")
                .put("content", context));

        for (AiChatDto aiChatDto : video.getAiChatHistory()) {
            messages.put(new JSONObject()
                    .put("role", aiChatDto.getRole())
                    .put("content", aiChatDto.getContent()));
        }

        messages.put(new JSONObject()
                .put("role", "user")
                .put("content", userPrompt));

        JSONObject json = new JSONObject();
        json.put("model", "llama3-8b-8192");
        json.put("messages", messages);


        return json.toString();
    }
}
