package escampe.heuristics;
import escampe.EscampeBoard;
import escampe.MoveUtil;

public class UnicornThreatHeuristic implements Heuristic {

    @Override
    public int evaluate(EscampeBoard b, String me, String opp) {
        // positif si moi je menace, négatif si l'autre menace
        int score = 0;
        if (canCaptureUnicornNext(b, me, opp))  score += 1;
        if (canCaptureUnicornNext(b, opp, me))  score -= 2; // danger > attaque
        return score;
    }

    public static boolean canCaptureUnicornNext(EscampeBoard b, String attacker, String defender) {
        String defUnicornSq = findUnicornSquare(b, defender);
        if (defUnicornSq == null) return true; // déjà capturée => terminal

        String[] moves = b.possiblesMoves(attacker);
        for (String m : moves) {
            if (MoveUtil.end(m).equals(defUnicornSq)) return true;
        }
        return false;
    }

    public static String findUnicornSquare(EscampeBoard b, String player) {
        char u = "blanc".equals(player) ? 'B' : 'N';
        for (int r = 0; r < 6; r++) {
            for (int c = 0; c < 6; c++) {
                if (b.getCell(r, c) == u) {
                    return MoveUtil.toSquare(r, c);
                }
            }
        }
        return null;
    }
}
