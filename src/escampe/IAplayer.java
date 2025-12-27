package escampe;
import escampe.algo.AlphaBetaSearch;
import escampe.heuristics.HeuristicPipeline;
import escampe.heuristics.UnicornMobilityHeuristic;
import escampe.heuristics.PressureHeuristic;
import escampe.heuristics.UnicornThreatHeuristic;
import escampe.heuristics.WinLossHeuristic;


public class IAplayer implements IJoueur {

    private int myColour;            // -1 blanc, 1 noir
    private String myPlayer;         // "blanc" / "noir"
    private String enemyPlayer;      // "noir" / "blanc"

    private EscampeBoard board;

    // Gestion du placement (1er coup du joueur)
    private boolean myPlacementDone = false;

    // utile si tu es BLANC : placer à l’opposé du NOIR
    // true -> noir en haut (lignes 1-2), false -> noir en bas (lignes 5-6)
    private Boolean blackOnTop = null;

    // Si tu testes avec Solo et que tu veux arrêter quand gameOver :
    // mets true en local, et laisse false pour le client tournoi/serveur.
    private static final boolean SOLO_MODE = false;

    // Algo + heuristiques
    private AlphaBetaSearch search;

    @Override
    public void initJoueur(int mycolour) {
        this.myColour = mycolour;
        this.myPlayer = (mycolour == IJoueur.BLANC) ? "blanc" : "noir";
        this.enemyPlayer = (mycolour == IJoueur.BLANC) ? "noir" : "blanc";

        this.board = new EscampeBoard();
        this.myPlacementDone = false;
        this.blackOnTop = null;

        // --- Choix des heuristiques (tu peux en enlever si tu veux) ---
        HeuristicPipeline heuristics = new HeuristicPipeline()
                .add(new WinLossHeuristic(), 1)           // victoire/défaite
                .add(new UnicornThreatHeuristic(), 80)    // menaces sur licorne
                .add(new UnicornMobilityHeuristic(), 5)          // mobilité (diff de coups)
                .add(new PressureHeuristic(), 2);         // optionnel : pression/distance

        // --- AlphaBeta depth 3 (bon compromis) ---
        this.search = new AlphaBetaSearch(3, heuristics);

        System.out.println("Init joueur: " + (myColour == IJoueur.BLANC ? "BLANC" : "NOIR"));
    }

    @Override
    public int getNumJoueur() {
        return myColour;
    }

    @Override
    public String choixMouvement() {

        // Pour Solo uniquement : arrêter quand la partie est finie
        if (SOLO_MODE && board.gameOver()) {
            return "xxxxx";
        }

        // 1) Placement initial (1er coup de ton joueur)
        if (!myPlacementDone) {
            String placement = computePlacement();

            // mise à jour plateau local
            board.play(placement, myPlayer);
            myPlacementDone = true;

            System.out.println("Je joue mon placement: " + placement);
            return placement;
        }

        // 2) Coup normal : utiliser Alpha-Beta + heuristiques
        String chosen = search.chooseMove(board, myPlayer, enemyPlayer);

        if (chosen == null || chosen.isBlank()) {
            // sécurité : si jamais l'algo n'a rien renvoyé
            return "E";
        }

        // appliquer sur notre plateau local
        board.play(chosen, myPlayer);

        System.out.println("Je joue: " + chosen);
        return chosen;
    }

    @Override
    public void mouvementEnnemi(String coup) {
        if (coup == null) return;
        coup = coup.trim();

        if (coup.isEmpty()) return;
        if (coup.equals("xxxxx")) return; // Solo

        // Certains codes utilisent "PASSE" au lieu de "E"
        if (coup.equalsIgnoreCase("PASSE")) {
            coup = "E";
        }

        // Si c’est un placement du NOIR, on déduit s’il est en haut ou en bas (pour notre placement BLANC)
        if (coup.contains("/") && enemyPlayer.equals("noir")) {
            blackOnTop = inferBlackOnTop(coup);
        }

        // Appliquer le coup adverse sur notre plateau local
        // "E" est accepté par ton EscampeBoard
        board.play(coup, enemyPlayer);

        System.out.println("Ennemi a joué: " + coup);
    }

    @Override
    public void declareLeVainqueur(int colour) {
        if (colour == myColour) System.out.println("J'ai gagné !");
        else System.out.println("J'ai perdu.");
    }

    @Override
    public String binoName() {
        return "Hajar - Yacine";
    }

    // ------------------ Placement ------------------

    private String computePlacement() {
        // Stratégie simple :
        // - si je suis NOIR : je choisis "haut" (ligne 1)
        // - si je suis BLANC : je choisis l'opposé de la position du NOIR (si connue)
        if (myPlayer.equals("noir")) {
            // Noir en haut
            blackOnTop = true;
            return "A1/B1/C1/D1/E1/F1";
        } else {
            // Blanc : opposé du noir
            // Normalement blackOnTop est connu car Noir joue avant Blanc.
            boolean noirEnHaut = (blackOnTop == null) ? true : blackOnTop;
            if (noirEnHaut) {
                return "A6/B6/C6/D6/E6/F6"; // noir en haut => blanc en bas
            } else {
                return "A1/B1/C1/D1/E1/F1"; // noir en bas => blanc en haut
            }
        }
    }

    private boolean inferBlackOnTop(String placement) {
        // placement exemple "A1/B1/.../F1"
        // si majorité des lignes sont 1 ou 2 => noir en haut
        // si majorité des lignes sont 5 ou 6 => noir en bas
        String[] parts = placement.split("/");
        int topCount = 0;
        int bottomCount = 0;

        for (String p : parts) {
            if (p.length() != 2) continue;
            char row = p.charAt(1); // '1'..'6'
            if (row == '1' || row == '2') topCount++;
            if (row == '5' || row == '6') bottomCount++;
        }
        return topCount >= bottomCount;
    }
}
