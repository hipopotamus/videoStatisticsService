package statisticsservice.domain.weeklyStats.contorller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static statisticsservice.utility.ApiDocumentUtils.getRequestPreProcessor;
import static statisticsservice.utility.ApiDocumentUtils.getResponsePreProcessor;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
class WeeklyStatsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("주간 인기 게시판 조회_성공")
    void weeklyTopBoardDetails_Success() throws Exception {

        // given
        LocalDate date = LocalDate.of(2024, 1, 1);

        // when
        ResultActions actions = mockMvc.perform(
                get("/weeklyStats/topBoards")
                        .param("date", date.toString())
                        .accept(MediaType.APPLICATION_JSON)
        );

        // then
        actions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.date").value(date.toString()))
                .andExpect(jsonPath("$.boardIdListByViews").isArray())
                .andExpect(jsonPath("$.boardIdListByPlaytime").isArray())
                .andDo(document("weeklyTopBoardDetails",
                        getRequestPreProcessor(),
                        getResponsePreProcessor(),
                        queryParameters(
                                parameterWithName("date").description("조회할 날짜").attributes(key("constraints").value("yyyy-MM-dd 형식"))
                        ),
                        responseFields(
                                fieldWithPath("id").description("주간 상위 게시판 ID"),
                                fieldWithPath("date").description("날짜"),
                                fieldWithPath("boardIdListByViews").description("조회수 기준 게시판 ID 리스트"),
                                fieldWithPath("boardIdListByPlaytime").description("재생시간 기준 게시판 ID 리스트")
                        )
                ));
    }
}