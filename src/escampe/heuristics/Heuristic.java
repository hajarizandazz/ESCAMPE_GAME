package escampe.heuristics;

import escampe.EscampeBoard;

public interface Heuristic {
    int evaluate(EscampeBoard b, String me, String opp);
}

