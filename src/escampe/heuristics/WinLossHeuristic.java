package escampe.heuristics;

import escampe.EscampeBoard;
public class WinLossHeuristic implements Heuristic {
    @Override
    public int evaluate(EscampeBoard b, String me, String opp) {
        if (!b.hasUnicorn(opp)) return 1_000_000;
        if (!b.hasUnicorn(me))  return -1_000_000;
        return 0;
    }
}
