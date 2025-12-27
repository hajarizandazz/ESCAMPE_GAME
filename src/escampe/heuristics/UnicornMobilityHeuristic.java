package escampe.heuristics;

import escampe.EscampeBoard;
import escampe.MoveUtil;

public class UnicornMobilityHeuristic implements Heuristic {

    @Override
    public int evaluate(EscampeBoard b, String me, String opp) {
        String myUnicornSq = UnicornThreatHeuristic.findUnicornSquare(b, me);
        if (myUnicornSq == null) return -1000;

        int count = 0;
        for (String m : b.possiblesMoves(me)) {
            if (MoveUtil.start(m).equals(myUnicornSq)) count++;
        }
        return count; // plus c'est grand, mieux c'est
    }
}

