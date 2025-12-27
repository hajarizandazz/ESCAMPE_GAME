package escampe.heuristics;

import java.util.LinkedHashMap;
import java.util.Map;
import escampe.EscampeBoard;

public class HeuristicPipeline implements Heuristic {

    private final Map<Heuristic, Integer> weights = new LinkedHashMap<>();

    public HeuristicPipeline add(Heuristic h, int weight) {
        weights.put(h, weight);
        return this;
    }

    @Override
    public int evaluate(EscampeBoard b, String me, String opp) {
        int sum = 0;
        for (Map.Entry<Heuristic, Integer> e : weights.entrySet()) {
            sum += e.getValue() * e.getKey().evaluate(b, me, opp);
        }
        return sum;
    }
}
