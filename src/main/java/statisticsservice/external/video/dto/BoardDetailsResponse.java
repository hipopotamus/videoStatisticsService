package statisticsservice.external.video.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BoardDetailsResponse {

    private String videoURL;

    private String title;

    private String content;

    private long views;

    private long breakPoints;

    private List<String> adURLs = new ArrayList<>();

    private List<Long> adTimes = new ArrayList<>();
}
