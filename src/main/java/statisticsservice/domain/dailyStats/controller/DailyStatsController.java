package statisticsservice.domain.dailyStats.controller;

import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.NoSuchJobException;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import statisticsservice.external.board.client.BoardServiceClient;
import statisticsservice.external.board.dto.BoardDetailsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/dailyStats")
@RequiredArgsConstructor
public class DailyStatsController {

    private final BoardServiceClient boardServiceClient;
    private final JobLauncher jobLauncher;
    private final JobRegistry jobRegistry;

    @GetMapping("/{boardId}")
    public ResponseEntity<BoardDetailsResponse> boardDetails(@PathVariable Long boardId) {

        BoardDetailsResponse boardDetailsResponse = boardServiceClient.BoardDetails(boardId);

        return new ResponseEntity<>(boardDetailsResponse, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<String> test() {
        return new ResponseEntity<>("Hello World!", HttpStatus.OK);
    }

    @PostMapping("/batch/{value}")
    public ResponseEntity<String> batchTest(@PathVariable String value) throws Exception {

        JobParameters jobParameters = new JobParametersBuilder()
                .addString("value", value)
                .toJobParameters();

        jobLauncher.run(jobRegistry.getJob("dailyStatsBatchJob"), jobParameters);

        return new ResponseEntity<>("OK Batch", HttpStatus.OK);
    }

}
