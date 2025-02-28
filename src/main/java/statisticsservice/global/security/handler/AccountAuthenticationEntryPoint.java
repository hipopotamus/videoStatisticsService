package statisticsservice.global.security.handler;

import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import statisticsservice.global.exception.ExceptionCode;
import statisticsservice.global.exception.dto.ErrorResponse;

import java.io.IOException;

public class AccountAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {

        ExceptionCode unAuthentication = ExceptionCode.UN_AUTHENTICATION;
        ErrorResponse unAuthException =
                new ErrorResponse("UnAuthentication", unAuthentication.getMessage(), unAuthentication.getCode());

        String authenticationExJson = new Gson().toJson(unAuthException);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(authenticationExJson);
    }
}
