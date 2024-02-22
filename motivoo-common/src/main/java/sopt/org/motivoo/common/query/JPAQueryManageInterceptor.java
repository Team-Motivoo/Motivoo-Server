package sopt.org.motivoo.common.query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JPAQueryManageInterceptor implements HandlerInterceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(JPAQueryManageInterceptor.class);

    private final JPAQueryInspector jpaQueryInspector;

    public JPAQueryManageInterceptor(JPAQueryInspector jpaQueryInspector) {
        this.jpaQueryInspector = jpaQueryInspector;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        jpaQueryInspector.start();
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        QueryInspectResult queryInspectResult = jpaQueryInspector.inspectResult();
        LOGGER.info("METHOD: [{}], URI: {}, QUERY_COUNT: {}, QUERY_EXECUTION_TIME: {} ms",
                request.getMethod(),
                request.getRequestURI(),
                queryInspectResult.getCount(),
                queryInspectResult.getTime())
        ;
        jpaQueryInspector.finish();
    }
}
