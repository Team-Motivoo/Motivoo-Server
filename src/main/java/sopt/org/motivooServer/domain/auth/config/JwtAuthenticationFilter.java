package sopt.org.motivooServer.domain.auth.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        try {
            final String token = getJwtFromRequest(request);
            jwtTokenProvider.validateToken(token);

            Long memberId = Long.parseLong(jwtTokenProvider.getPayload(token));
            // authentication 객체 생성 -> principal에 유저정보를 담는다.
            UserAuthentication authentication = new UserAuthentication(memberId.toString(), null, null);
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (NumberFormatException e) {
            log.error("refresh token은 유저 아이디를 담고있지 않습니다.");
            throw new RuntimeException("refresh token은 유저 아이디를 담고있지 않습니다.");
        } catch (Exception e) {
            log.error("Spring Security doFilter 중에 발생한 에러: {}", e);
            throw new RuntimeException("Spring Security doFilter 중에 발생한 에러");
        }

        // 다음 필터로 요청 전달
        filterChain.doFilter(request, response);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring("Bearer ".length());
        }
        return null;
    }
}