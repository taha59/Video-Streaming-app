package com.programming.taha.Youtubeclone.service;

import lombok.RequiredArgsConstructor;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.services.transcribe.TranscribeClient;
import software.amazon.awssdk.services.transcribe.model.*;
import software.amazon.awssdk.services.transcribestreaming.model.LanguageCode;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class TranscriptionService {

    private final TranscribeClient transcribeClient = TranscribeClient.create();

    @Async
    public CompletableFuture<String> getTranscriptionFromMediaFile(String s3Url){

        String transcriptionJobName = "transcription_" + UUID.randomUUID();
        String mediaType = "mp4"; // can be other types
        Media myMedia = Media.builder()
                .mediaFileUri(s3Url)
                .build();

        // Create the transcription job request
        StartTranscriptionJobRequest request = StartTranscriptionJobRequest.builder()
                .transcriptionJobName(transcriptionJobName)
                .languageCode(LanguageCode.EN_US.toString())
                .mediaFormat(mediaType)
                .media(myMedia)
                .build();

        // send the request to start the transcription job
        StartTranscriptionJobResponse startJobResponse = transcribeClient.startTranscriptionJob(request);

        System.out.println("Created the transcription job");
        System.out.println(startJobResponse.transcriptionJob());

        // Create the get job request
        GetTranscriptionJobRequest getJobRequest = GetTranscriptionJobRequest.builder()
                .transcriptionJobName(transcriptionJobName)
                .build();

        // send the request to get the transcription job including the job status
        GetTranscriptionJobResponse getJobResponse = null;

        System.out.println("Get the transcription job request");

        boolean jobResultsRecieved = false;

        // keep polling for job results until the job has either completed or failed
        while (!jobResultsRecieved) {
            getJobResponse = transcribeClient.getTranscriptionJob(getJobRequest);

            String jobStatus = getJobResponse
                    .transcriptionJob()
                    .transcriptionJobStatus()
                    .name();

            if (jobStatus.equalsIgnoreCase(TranscriptionJobStatus.COMPLETED.name())) {
                jobResultsRecieved = true;
            } else if (jobStatus.equalsIgnoreCase(TranscriptionJobStatus.FAILED.name())) {
                jobResultsRecieved = true;
            } else {
                //poll every 10 seconds until the job results are received
                try {
                    System.out.println("polling...");
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    System.out.println(e.getMessage());
                }
            }
        }

        //extract the transcript file uri
        String transcriptUri = getJobResponse
                .transcriptionJob()
                .transcript()
                .transcriptFileUri();

        //delete the job after receiving job results
        DeleteTranscriptionJobRequest deleteTranscriptionJobRequest = DeleteTranscriptionJobRequest.builder()
                .transcriptionJobName(transcriptionJobName)
                .build();

        transcribeClient.deleteTranscriptionJob(deleteTranscriptionJobRequest);

        return CompletableFuture.completedFuture(extractTranscription(transcriptUri));
    }

    private String extractTranscription(String transcriptUri){
        try {
            //Connect to the transcript URI and download the JSON content
            URL url = new URL(transcriptUri);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuilder content = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }

            in.close();
            conn.disconnect();

            // Parse the JSON to retrieve the transcription text
            JSONObject json = new JSONObject(content.toString());

            String transcript = json.getJSONObject("results")
                    .getJSONArray("transcripts")
                    .getJSONObject(0)
                    .getString("transcript");

            System.out.println("Transcription Extracted!");

            return transcript;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
