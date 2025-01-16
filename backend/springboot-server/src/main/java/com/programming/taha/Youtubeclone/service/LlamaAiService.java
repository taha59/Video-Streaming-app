package com.programming.taha.Youtubeclone.service;
import com.programming.taha.Youtubeclone.dto.AiChatDto;
import com.programming.taha.Youtubeclone.model.Video;
import com.programming.taha.Youtubeclone.repository.VideoRepository;
import lombok.RequiredArgsConstructor;

import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.*;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;
import org.apache.hc.core5.http.io.support.ClassicRequestBuilder;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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
    private final VideoRepository videoRepository;

    public String startChat(String userPrompt, Video video, String userId){

        String transcript = video.getTranscription();
        if (transcript == null || transcript.isEmpty()){
            throw new RuntimeException("Transcript not found!!");
        }

        String body = prepareBody(userPrompt, video, userId);

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

            video.aiChatHistoryMapInsert(userId, new AiChatDto("user", userPrompt));
            video.aiChatHistoryMapInsert(userId, new AiChatDto("assistant", aiResponse));

            return aiResponse;
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    public void deleteAiChatHistory(Video video, String userId){
        video.clearAiChatHistory(userId);
    }

    @Async
    public void getVideoTranscript(MultipartFile multipartFile, String videoId){

        ClassicHttpRequest httpRequest = ClassicRequestBuilder
                .post("https://api.groq.com/openai/v1/audio/transcriptions")
                .addHeader("Authorization", "Bearer " + groq_API_KEY)
                .build();

        // Build multipart entity
        try {
            HttpEntity entity = MultipartEntityBuilder.create()
                    .addBinaryBody("file", multipartFile.getInputStream(),
                                   ContentType.MULTIPART_FORM_DATA,
                                   multipartFile.getOriginalFilename())
                    .addTextBody("model", "whisper-large-v3", ContentType.TEXT_PLAIN)
                    .build();

            httpRequest.setEntity(entity);

            HttpClientResponseHandler<String> responseHandler = classicHttpResponse -> {

                String s = new String(classicHttpResponse.getEntity().getContent().readAllBytes());
                classicHttpResponse.close();
                return s;
            };
            try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
                String videoTranscript = httpClient.execute(httpRequest, responseHandler);

                videoTranscript = new JSONObject(videoTranscript).getString("text");

                Video video = videoRepository.findById(videoId)
                        .orElseThrow(() -> new IllegalArgumentException( "Video id Not Found!!" + videoId ));

                video.setTranscription(videoTranscript);
                videoRepository.save(video);

            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private static String prepareBody(String userPrompt, Video video, String userId) {

        String context = String.format("You are an AI assistant helping users ask questions " +
                "about the video. Your job is to answer questions strictly related to the " +
                "video's content. If a user asks a question unrelated to the video, politely " +
                "remind them to ask relevant questions. Do not mention or reference the word " +
                "'transcript' in your responses. Do not offer advice or engage in topics outside" +
                " the video's content, even if asked."
                +
                "Video transcript: transcript: %s", video.getTranscription());

        //Provide context to AI for answering user questions
        JSONArray messages = new JSONArray();
        messages.put(new JSONObject()
                .put("role", "system")
                .put("content", context));

        //provide chat history to the AI for conversational awareness
        for (AiChatDto aiChatDto : video.getUserAiChatHistory(userId)) {
            messages.put(new JSONObject()
                    .put("role", aiChatDto.getRole())
                    .put("content", aiChatDto.getContent()));
        }

        //user prompt for the AI to answer to
        messages.put(new JSONObject()
                .put("role", "user")
                .put("content", userPrompt));

        JSONObject json = new JSONObject();
        json.put("model", "llama3-8b-8192");
        json.put("messages", messages);


        return json.toString();
    }
}
