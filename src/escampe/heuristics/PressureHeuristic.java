package escampe.heuristics;
import escampe.EscampeBoard;
import escampe.MoveUtil;

public class PressureHeuristic implements Heuristic {
    @Override
    public int evaluate(EscampeBoard b, String me, String opp) {
        String enemyU = UnicornThreatHeuristic.findUnicornSquare(b, opp);
        if (enemyU == null) return 1000;

        int ur = MoveUtil.row(enemyU);
        int uc = MoveUtil.col(enemyU);

        int best = 999;
        for (int r = 0; r < 6; r++) {
            for (int c = 0; c < 6; c++) {
                char p = b.getCell(r, c);
                if (isMyPiece(p, me)) {
                    int d = Math.abs(r - ur) + Math.abs(c - uc);
                    best = Math.min(best, d);
                }
            }
        }
        // plus best est petit => mieux
        return -best;
    }

    private boolean isMyPiece(char p, String me) {
        if ("blanc".equals(me)) return p == 'b' || p == 'B';
        return p == 'n' || p == 'N';
    }
}

