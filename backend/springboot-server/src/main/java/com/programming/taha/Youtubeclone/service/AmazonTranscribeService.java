package com.programming.taha.Youtubeclone.service;

import com.programming.taha.Youtubeclone.model.Video;
import com.programming.taha.Youtubeclone.repository.VideoRepository;
import lombok.RequiredArgsConstructor;

import org.json.JSONObject;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import software.amazon.awssdk.services.transcribe.TranscribeClient;
import software.amazon.awssdk.services.transcribe.model.*;
import software.amazon.awssdk.services.transcribestreaming.model.LanguageCode;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AmazonTranscribeService {

    private final TranscribeClient transcribeClient = TranscribeClient.create();

    private final VideoRepository videoRepository;

    @Async
    public void getVideoTranscript(String s3Url, String videoId){

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

        Video video = videoRepository.findById(videoId)
                .orElseThrow(() -> new IllegalArgumentException( "Video id Not Found!!" + videoId ));

        video.setTranscription(extractTranscription(transcriptUri));
        videoRepository.save(video);
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
