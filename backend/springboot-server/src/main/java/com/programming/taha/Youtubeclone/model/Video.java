package com.programming.taha.Youtubeclone.model;

import com.programming.taha.Youtubeclone.dto.AiChatDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

@Document(value = "video")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Video {
    @Id
    private String id;
    private String title;
    private String description;
    private String transcription;
    private String userId;
    private ConcurrentHashMap<String, List<AiChatDto>> aiChatHistoryMap = new ConcurrentHashMap<>();
    private AtomicInteger likes = new AtomicInteger(0);
    private AtomicInteger dislikes = new AtomicInteger(0);
    private Set<String> tags;
    private String videoUrl;
    private VideoStatus videoStatus;
    private AtomicInteger viewCount = new AtomicInteger(0);
    private String thumbnailUrl;
    private List<Comment> commentList = new CopyOnWriteArrayList<>();

    @CreatedDate
    private Instant createdDate;

    public void incrementLikes(){
        likes.incrementAndGet();
    }

    public void decrementLikes(){
        likes.decrementAndGet();
    }

    public void incrementDisLikes(){
        dislikes.incrementAndGet();
    }

    public void decrementDislikes(){
        dislikes.decrementAndGet();
    }

    public void incrementViewCount() { viewCount.incrementAndGet(); }

    public void addComment(Comment comment) {
        commentList.add(comment);
    }

    public void deleteAllComments(){
        commentList.clear();
    }

    public void clearAiChatHistory(String userId){ aiChatHistoryMap.get(userId).clear(); }

    public void aiChatHistoryMapInsert(String userId, AiChatDto aiChatDto){
        List<AiChatDto> aiChatDtoList = aiChatHistoryMap.get(userId);

        if (aiChatDtoList == null){
            aiChatDtoList = new CopyOnWriteArrayList<>();
            aiChatDtoList.add(aiChatDto);
        }
        else {
            aiChatDtoList.add(aiChatDto);
        }
        aiChatHistoryMap.put(userId, aiChatDtoList);
    }

    public List<AiChatDto> getUserAiChatHistory(String userId){
        return aiChatHistoryMap.getOrDefault(userId, new CopyOnWriteArrayList<>());
    }


}
