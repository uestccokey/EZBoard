package cn.ezandroid.goboard.demo;

/**
 * Debug
 *
 * @author like
 * @date 2017-07-18
 */
public class Debug {

    public static void printBoard(byte[] board) {
        System.err.println("Board:");
        System.err.print("|-");
        for (int i = 0; i < 19; i++) {
            System.err.print("--");
        }
        System.err.print("|");
        System.err.println();
        for (int i = 0; i < 19; i++) {
            for (int j = 0; j < 21; j++) {
                if (j == 0 || j == 20) {
                    System.err.print("| ");
                } else {
                    int player = board[i * 19 + (j - 1)];
                    if (player == FeatureBoard.BLACK) {
                        System.err.print("○");
                    } else if (player == FeatureBoard.WHITE) {
                        System.err.print("●");
                    } else {
                        System.err.print("+");
                    }
                    System.err.print(" ");
                }
            }
            System.err.println();
        }
        System.err.print("|-");
        for (int i = 0; i < 19; i++) {
            System.err.print("--");
        }
        System.err.print("|");
        System.err.println();
    }

    public static void printBoardNew(byte[] board) {
        System.err.println("Board:");
        System.err.print("|-");
        for (int i = 0; i < 19; i++) {
            System.err.print("--");
        }
        System.err.print("|");
        System.err.println();
        for (int i = 0; i < 19; i++) {
            for (int j = 0; j < 21; j++) {
                if (j == 0 || j == 20) {
                    System.err.print("| ");
                } else {
                    int player = board[i * 19 + (j - 1)];
                    if (player == FeatureBoard.BLACK) {
                        System.err.print("B");
                    } else if (player == FeatureBoard.WHITE) {
                        System.err.print("W");
                    } else {
                        System.err.print("+");
                    }
                    System.err.print(" ");
                }
            }
            System.err.println();
        }
        System.err.print("|-");
        for (int i = 0; i < 19; i++) {
            System.err.print("--");
        }
        System.err.print("|");
        System.err.println();
    }

    public static void printRate(float[] rate) {
        System.err.println("Rate:");
        System.err.print("|");
        for (int i = 0; i < 19; i++) {
            System.err.print("-----");
        }
        System.err.print("|");
        System.err.println();
        for (int i = 0; i < 19; i++) {
            for (int j = 0; j < 21; j++) {
                if (j == 0 || j == 20) {
                    System.err.print("|");
                } else {
                    float value = rate[i * 19 + (j - 1)] * 1000 / 10f;
                    if (value < 10.0) {
                        System.err.print(" " + String.format("%.1f", value));
                    } else if (value > 99.9) {
                        System.err.print(" 100");
                    } else {
                        System.err.print(String.format("%.1f", value));
                    }
                    System.err.print(" ");
                }
            }
            System.err.println();
        }
        System.err.print("|");
        for (int i = 0; i < 19; i++) {
            System.err.print("-----");
        }
        System.err.print("|");
        System.err.println();
    }
}
