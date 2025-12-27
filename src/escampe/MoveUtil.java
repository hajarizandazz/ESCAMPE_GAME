package escampe;

public class MoveUtil {

    // "A1-B2" -> start "A1", end "B2"
    public static String start(String move) {
        return move.substring(0, 2);
    }

    public static String end(String move) {
        return move.substring(3, 5);
    }

    public static int col(String sq) { // 'A'..'F' -> 0..5
        return sq.charAt(0) - 'A';
    }

    public static int row(String sq) { // '1'..'6' -> 0..5
        return sq.charAt(1) - '1';
    }

    public static String toSquare(int r, int c) {
        return "" + (char)('A' + c) + (char)('1' + r);
    }
}
