package sopt.org.motivooServer.global.query;

public class QueryInspectResult {

    private final int count;
    private final long time;

    public QueryInspectResult(int count, long time) {
        this.count = count;
        this.time = time;
    }

    public int getCount() {
        return count;
    }

    public long getTime() {
        return time;
    }
}
