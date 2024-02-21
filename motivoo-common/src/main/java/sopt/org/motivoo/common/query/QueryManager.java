package sopt.org.motivoo.common.query;

import java.util.ArrayList;
import java.util.List;

public class QueryManager {

    private static final String QUERY_TO_INSPECT_FOR_N_PLUS_ONE = "select";
    private static final int MAX_QUERY_COUNT = 10;

    private final List<String> queries;
    private final long time;

    public QueryManager(List<String> queries, long time) {
        this.queries = queries;
        this.time = time;
    }

    public void addQuery(String sql) {
        queries.add(sql);
    }

    public List<Integer> extractIndexOfSelectQuery() {
        List<Integer> indexOfSelectQuery = new ArrayList<>();
        for (int index = 0; index < queries.size(); index++) {
            if (queries.get(index).contains(QUERY_TO_INSPECT_FOR_N_PLUS_ONE)) {
                indexOfSelectQuery.add(index);
            }
        }
        return indexOfSelectQuery;
    }

    public boolean isOverThanMaxQueryCount() {
        return getQueryCount() >= MAX_QUERY_COUNT;
    }

    public int getQueryCount() {
        return queries.size();
    }

    public long calculateDuration(long afterQuery) {
        return afterQuery - time;
    }
}
