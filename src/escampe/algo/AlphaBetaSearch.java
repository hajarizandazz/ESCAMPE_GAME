package escampe.algo;

import java.util.Arrays;
import escampe.EscampeBoard;
import escampe.heuristics.Heuristic;
import escampe.heuristics.UnicornThreatHeuristic;
import escampe.MoveUtil;


public class AlphaBetaSearch {

    private final int depth;
    private final Heuristic eval;

    private static final int INF = 1_000_000_000;

    public AlphaBetaSearch(int depth, Heuristic eval) {
        this.depth = depth;
        this.eval = eval;
    }

    public String chooseMove(EscampeBoard state, String me, String opp) {
        String[] moves = state.possiblesMoves(me);
        if (moves.length == 0) return "E";

        // Move ordering simple : tester d'abord les coups qui capturent la licorne
        Arrays.sort(moves, (a, b) -> Integer.compare(
                orderScore(state, b, me, opp),
                orderScore(state, a, me, opp)
        ));

        int bestScore = -INF;
        String bestMove = moves[0];

        for (String m : moves) {
            EscampeBoard next = state.copy();
            next.play(m, me);

            int score = alphabeta(next, depth - 1, -INF, INF, false, me, opp);
            if (score > bestScore) {
                bestScore = score;
                bestMove = m;
            }
        }
        return bestMove;
    }

    private int alphabeta(EscampeBoard state, int d, int alpha, int beta, boolean maximizing,
                          String me, String opp) {

        if (d == 0 || state.gameOver()) {
            return eval.evaluate(state, me, opp);
        }

        String current = maximizing ? me : opp;
        String[] moves = state.possiblesMoves(current);

        if (moves.length == 0) {
            // passe
            return alphabeta(state, d - 1, alpha, beta, !maximizing, me, opp);
        }

        Arrays.sort(moves, (a, b) -> Integer.compare(
                orderScore(state, b, current, current.equals(me) ? opp : me),
                orderScore(state, a, current, current.equals(me) ? opp : me)
        ));

        if (maximizing) {
            int value = -INF;
            for (String m : moves) {
                EscampeBoard next = state.copy();
                next.play(m, current);

                value = Math.max(value, alphabeta(next, d - 1, alpha, beta, false, me, opp));
                alpha = Math.max(alpha, value);
                if (alpha >= beta) break;
            }
            return value;
        } else {
            int value = INF;
            for (String m : moves) {
                EscampeBoard next = state.copy();
                next.play(m, current);

                value = Math.min(value, alphabeta(next, d - 1, alpha, beta, true, me, opp));
                beta = Math.min(beta, value);
                if (alpha >= beta) break;
            }
            return value;
        }
    }

    private int orderScore(EscampeBoard s, String move, String attacker, String defender) {
        // 1) capture licorne d'abord
        String defU = UnicornThreatHeuristic.findUnicornSquare(s, defender);
        if (defU != null && MoveUtil.end(move).equals(defU)) return 1_000_000;

        // 2) sinon, petite préférence pour la mobilité après coup
        EscampeBoard next = s.copy();
        next.play(move, attacker);
        return next.possiblesMoves(attacker).length;
    }
}
