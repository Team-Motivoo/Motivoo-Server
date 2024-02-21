package sopt.org.motivoo.common.query;

import java.util.ArrayList;

import org.hibernate.resource.jdbc.spi.StatementInspector;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class JPAQueryInspector implements StatementInspector {

    private static final Logger LOGGER = LoggerFactory.getLogger(JPAQueryInspector.class);

    private static final ThreadLocal<QueryManager> queryManagers = new ThreadLocal<>();

    void start() {
        queryManagers.set(new QueryManager(
                new ArrayList<>(),
                System.currentTimeMillis()
        ));
    }

    void finish() {
        queryManagers.remove();
    }

    @Override
    public String inspect(String sql) {
        LOGGER.info("🚀sql: {}", sql);
        QueryManager queryManager = queryManagers.get();
        if (queryManager != null) {
            queryManager.addQuery(sql);
        }
        return sql;
    }

    public QueryInspectResult inspectResult() {
        QueryManager queryManager = queryManagers.get();
        long queryDurationTime = queryManager.calculateDuration(System.currentTimeMillis());
        checkQueryCountIsOverThanMaxCount(queryManager);
        return new QueryInspectResult(queryManager.getQueryCount(), queryDurationTime);
    }

    private void checkQueryCountIsOverThanMaxCount(@NotNull QueryManager queryManager) {
        if (queryManager.isOverThanMaxQueryCount()) {
            LOGGER.warn("🚨쿼리가 10번 이상 실행되었습니다");
            checkIsSusceptibleToNPlusOne(queryManager);
        }
    }

    private void checkIsSusceptibleToNPlusOne(@NotNull QueryManager queryManager) {
        NPlusOneDetector nPlusOneDetector = new NPlusOneDetector(queryManager.extractIndexOfSelectQuery());
        if (nPlusOneDetector.isSelectCountOverThanWarnCount() && nPlusOneDetector.detect()) {
            LOGGER.warn("🚨select 문이 연속해서 5회 이상 실행되었습니다. N+1 문제일 수 있습니다");
        }
    }
}
