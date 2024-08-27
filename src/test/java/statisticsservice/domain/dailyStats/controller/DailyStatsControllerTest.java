package statisticsservice.domain.dailyStats.controller;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
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
class DailyStatsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Value("${jwt.secret-key}")
    String secretKey;

    @Value("${jwt.expiration}")
    long expiration;

    @Test
    @DisplayName("일일 수익 조회_성공")
    void dailyRevenueDetails_Success() throws Exception {

        // given
        Long loginId = 10001L;
        String jwt = "Bearer " + createAuthJwtToken(loginId);
        LocalDate date = LocalDate.of(2024, 1, 1);

        // when
        ResultActions actions = mockMvc.perform(
                get("/dailyStats/revenue")
                        .param("date", date.toString())
                        .header("Authorization", jwt)
                        .accept(MediaType.APPLICATION_JSON)
        );

        // then
        actions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountId").value(loginId))
                .andExpect(jsonPath("$.revenue").isNumber())
                .andExpect(jsonPath("$.videoRevenueList").isArray())
                .andDo(document("dailyRevenueDetails",
                        getRequestPreProcessor(),
                        getResponsePreProcessor(),
                        requestHeaders(
                                headerWithName("Authorization").description("JWT").attributes(key("constraints").value("JWT Form"))
                        ),
                        queryParameters(
                                parameterWithName("date").description("조회할 날짜").attributes(key("constraints").value("yyyy-MM-dd 형식"))
                        ),
                        responseFields(
                                fieldWithPath("accountId").description("계정 ID"),
                                fieldWithPath("revenue").description("총 수익"),
                                fieldWithPath("videoRevenueList").description("게시물별 수익 리스트"),
                                fieldWithPath("videoRevenueList[].boardId").description("게시물 ID"),
                                fieldWithPath("videoRevenueList[].totalRevenue").description("해당 게시물의 총 수익"),
                                fieldWithPath("videoRevenueList[].videoRevenue").description("해당 영상의 수익"),
                                fieldWithPath("videoRevenueList[].adVideoRevenue").description("해당 광고 영상의 수익")
                        )
                ));
    }

    @Test
    @DisplayName("일일 인기 게시판 조회_성공")
    void dailyTopBoardDetails_Success() throws Exception {

        // given
        LocalDate date = LocalDate.of(2024, 1, 1);

        // when
        ResultActions actions = mockMvc.perform(
                get("/dailyStats/topBoards")
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
                .andDo(document("dailyTopBoardDetails",
                        getRequestPreProcessor(),
                        getResponsePreProcessor(),
                        queryParameters(
                                parameterWithName("date").description("조회할 날짜").attributes(key("constraints").value("yyyy-MM-dd 형식"))
                        ),
                        responseFields(
                                fieldWithPath("id").description("일일 상위 게시판 ID"),
                                fieldWithPath("date").description("날짜"),
                                fieldWithPath("boardIdListByViews").description("조회수 기준 게시판 ID 리스트"),
                                fieldWithPath("boardIdListByPlaytime").description("재생시간 기준 게시판 ID 리스트")
                        )
                ));
    }

    public String createAuthJwtToken(Long loginId) {

        Map<String, Object> claims = createClaims("test@test.com");

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(loginId.toString())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes()))
                .compact();
    }

    private Map<String, Object> createClaims(String email) {

        Map<String, Object> claims = new HashMap<>();

        claims.put("email", email);
        claims.put("role", List.of("USER"));

        return claims;
    }
}