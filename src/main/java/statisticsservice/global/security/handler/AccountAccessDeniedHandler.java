package statisticsservice.global.security.handler;

import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import statisticsservice.global.exception.ExceptionCode;
import statisticsservice.global.exception.dto.ErrorResponse;

import java.io.IOException;

public class AccountAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException, ServletException {

        ExceptionCode forbidden = ExceptionCode.FORBIDDEN;
        ErrorResponse accessException =
                new ErrorResponse("Forbidden", forbidden.getMessage(), forbidden.getCode());

        String authenticationExJson = new Gson().toJson(accessException);
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(authenticationExJson);
    }
}
