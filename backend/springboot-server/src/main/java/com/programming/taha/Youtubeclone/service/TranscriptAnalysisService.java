package com.programming.taha.Youtubeclone.service;

import lombok.RequiredArgsConstructor;
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
public class TranscriptAnalysisService {

    @Value("${groq.api.key}")
    private String groq_API_KEY;

    public String analyzeTranscript(String transcript){

        //set the body for a http request to the groq api
        String body = String.format("""
        {
            "model": "llama3-8b-8192",
            "messages": [
                {
                    "role": "system",
                    "content": "You are a video streaming platform. Based on the transcription of a video, summarize the video's main content and key takeaways in a concise and neutral tone."
                },
                {
                    "role": "user",
                    "content": "%s"
                }
            ]
        }
        """, transcript);

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

            //retrieve and parse the json response to get the AI overview of the transcript
            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            JSONObject jsonObject = new JSONObject(response.body());

            return jsonObject
                    .getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content");
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

    }
}
