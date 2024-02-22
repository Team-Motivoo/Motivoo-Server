package sopt.org.motivoo.common.query;

import java.util.List;

public class NPlusOneDetector {

    private static final int N_PLUS_ONE_WARNING_COUNT = 5;
    private static final int SIGNAL_OF_IN_A_ROW = 1;

    private final List<Integer> values;

    public NPlusOneDetector(List<Integer> values) {
        this.values = values;
    }

    public boolean detect() {
        for (int i = 0; i <= values.size() - N_PLUS_ONE_WARNING_COUNT; i++) {
            if (isSequential(values.subList(i, i + N_PLUS_ONE_WARNING_COUNT))) {
                return true;
            }
        }
        return false;
    }

    private boolean isSequential(List<Integer> list) {
        for (int i = 0; i < N_PLUS_ONE_WARNING_COUNT - 1; i++) {
            if (list.get(i + 1) - list.get(i) != SIGNAL_OF_IN_A_ROW) {
                return false;
            }
        }
        return true;
    }

    public boolean isSelectCountOverThanWarnCount() {
        return values.size() >= N_PLUS_ONE_WARNING_COUNT;
    }
}
