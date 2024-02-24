package nl.tudelft.sem.template.example.dtos.review;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewDetailsResponse {
    private Long reviewId;
    private Long bookId;
    private Long userId;
    private Float rating;
    private List<Long> upvoteUsers;
    private List<Long> downvoteUsers;
    private List<Long> comments;
    @JsonProperty("date-created")
    private String dateCreated;
    @JsonProperty("date-edited")
    private String dateEdited;
    private List<Long> reports;
    private String text;
}
