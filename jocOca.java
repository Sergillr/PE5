import java.util.Scanner;
import java.util.InputMismatchException;
import java.lang.IllegalStateException;
public class jocOca {

    // COLORS ANSI
    static final String RESET = "\u001B[0m";
    static final String RED = "\u001B[31m";
    static final String GREEN = "\u001B[32m";
    static final String YELLOW = "\u001B[33m";
    static final String BLUE = "\u001B[34m";
    static final String PURPLE = "\u001B[35m";
    static final String CYAN = "\u001B[36m";

    // VARIABLES DEL JOC
    static int numPlayers;
    static final int numCells = 63;

    // ESTATS DELS JUGADORS
    static int[] positions;
    static String[] names;
    static int[] skip;

    // ESTAT DEL JOC
    static int turn = 0;
    static boolean winner = false;

    // SCANNER
    static Scanner scan = new Scanner(System.in);

    // MÈTODE PRINCIPAL
    public static void main(String[] args) {

        try {
            getNumberPlayers();
            scan.nextLine();

            names = new String[numPlayers];
            positions = new int[numPlayers];
            skip = new int[numPlayers];

            getPlayersName();
            chooseFirstPlayer();
            mainGame();

            int winnerIndex = (turn - 1) % numPlayers;
            System.out.println(PURPLE + "\n¡¡" + names[winnerIndex] + " ha ganado la partida!!" + RESET);
        } catch (InputMismatchException e) {
            System.out.println(RED + "Entrada inválida." + RESET);
        } catch (IllegalStateException e) {
            System.out.println(RED + "Scanner cerrado incorrectamente." + RESET);
        } catch (ArithmeticException e) {
            System.out.println(RED + "Error aritmético." + RESET);
        } catch (Exception e) {
            System.out.println(RED + "Error inesperado. Fin del juego." + RESET);
        }
    }

    // BUCLE PRINCIPAL DEL JOC
    private static void mainGame() {
        while (!winner) {

            int currentPlayer = turn % numPlayers;
            System.out.println(CYAN + "\n--- Turno " + (turn + 1) + " | " + names[currentPlayer] + " ---" + RESET);

            playTurn(currentPlayer);

            if (checkWinner()) {
                winner = true;
                break;
            }

            turn++;
        }
    }

    // MÈTODES D'INICIALITZACIÓ
    private static void getPlayersName() {
        for (int i = 0; i < numPlayers; i++) {
            boolean ok = false;
            do {
                try {
                    System.out.print("Nombre del jugador " + (i + 1) + ": ");
                    names[i] = scan.nextLine();
                    ok = true;
                } catch (InputMismatchException e) {
                    scan.nextLine();
                } catch (IllegalStateException e) {
                    scan.nextLine();
                } catch (Exception e) {
                    scan.nextLine();
                }
            } while (!ok);
        }
    }

    // MÈTODES DEL JOC
    private static void getNumberPlayers() {
        boolean ok;
        do {
            ok = true;
            try {
                System.out.print("Número de jugadores (2 a 4): ");
                numPlayers = scan.nextInt();
            } catch (InputMismatchException e) {
                scan.nextLine();
                ok = false;
                numPlayers = 0;
            } catch (IllegalStateException e) {
                ok = false;
                numPlayers = 0;
            } catch (Exception e) {
                scan.nextLine();
                ok = false;
                numPlayers = 0;
            }
        } while (!ok || numPlayers < 2 || numPlayers > 4);
    }

    static void chooseFirstPlayer() {
        int maxSum = -1;
        int first = 0;

        for (int i = 0; i < numPlayers; i++) {
            System.out.println(BLUE + "\n" + names[i] + " tira dos dados para decidir el orden." + RESET);
            int sum = rollDice(i);

            if (sum > maxSum) {
                maxSum = sum;
                first = i;
            }
        }

        turn = first;
        System.out.println(PURPLE + "\nEmpieza la partida: " + names[first] + RESET);
    }

    // MÈTODES DELS TURNS
    static void playTurn(int player) {
        // COMPROVACIÓ DE PÈRDUA DE TURNOS
        if (skip[player] > 0) {
            System.out.println(RED + names[player] + " pierde este turno." + RESET);
            skip[player]--;
            return;
        }
        // TIRADA AMB DOS DAUS
        int diceSum = rollDice(positions[player]);

        if (turn < numPlayers) {
            firstTurn(player, diceSum);
        } else {
            processMove(player, diceSum);
        }

        System.out.println(GREEN + "Posición actual: " + positions[player] + RESET);
    }

    // MÈTODES DE TIRADA DE DOS DAUS
    static int rollDice(int currentPosition) {

        int sum = 0;
        // TIRADA AMB UN SOL DAU A PARTIR DE LA CASILLA 60
        if (currentPosition >= 60) {
            try {
                System.out.print(BLUE + "Casilla " + currentPosition + ": tiras 1 dado..." + RESET);
                scan.nextLine();
            } catch (InputMismatchException e) {
                scan.nextLine();
            } catch (IllegalStateException e) {
                scan.nextLine();
            } catch (Exception e) {
                scan.nextLine();
            }
            int d = rollSingleDie();
            System.out.println(BLUE + "Dado: " + d + RESET);
            return d;
        }
        // TIRADA AMB DOS DAUS
        for (int i = 1; i <= 2; i++) {
            try {
                System.out.print(BLUE + "Pulsa ENTER para tirar el dado " + i + "..." + RESET);
                scan.nextLine();
            } catch (InputMismatchException e) {
                scan.nextLine();
            } catch (IllegalStateException e) {
                scan.nextLine();
            } catch (Exception e) {
                scan.nextLine();
            }
            int d = rollSingleDie();
            System.out.println(BLUE + "Dado " + i + ": " + d + RESET);
            sum += d;
        }

        System.out.println(BLUE + "Suma total: " + sum + RESET);
        return sum;
    }

    // TIRADA D'UN SOL DAU
    static int rollSingleDie() {
        return (int) (Math.random() * 6) + 1;
    }

    // MÈTODES DE PRIMERA TIRADA
    static void firstTurn(int player, int sum) {

        System.out.println(YELLOW + "Primer turno, suma: " + sum + RESET);

        if (sum == 9) {
            positions[player] = 26;
            System.out.println(YELLOW + "De dado a dado y tiro porque me ha tocado." + RESET);
            return;
        }

        if (sum == 6) {
            positions[player] = 53;
            System.out.println(YELLOW + "Casilla especial dados 4-5." + RESET);
            return;
        }

        positions[player] = sum;
    }

    // MÈTODES DE MOVIMENT I COMPROVACIÓ DE CASILLES
    static void processMove(int player, int advance) {

        System.out.println(GREEN + names[player] + " avanza " + advance + " casillas." + RESET);
        positions[player] += advance;

        if (positions[player] > numCells) {
            int excess = positions[player] - numCells;
            positions[player] = numCells - excess;
            System.out.println(RED + "Rebote: retrocedes a " + positions[player] + RESET);
        }

        checkCells(player);
    }

    // MÈTODES DE COMPROVACIÓ DE CASILLES ESPECIALS
    static void checkCells(int player) {

        boolean repeat;

        do {
            repeat = false;
            int cell = positions[player];

            switch (cell) {

                case 5: case 9: case 14: case 18: case 23:
                case 27: case 32: case 36: case 41: case 45:
                case 50: case 54: case 59:
                    System.out.println(YELLOW + "De oca en oca y tiro porque me toca." + RESET);
                    positions[player] = nextGoose(cell);
                    repeat = true;
                    break;
                // PONTS
                case 6:
                case 12:
                    System.out.println(YELLOW + "De puente a puente y tiro porque me lleva la corriente." + RESET);
                    positions[player] = (cell == 6) ? 12 : 6;
                    repeat = true;
                    break;

                //FONDA
                case 19:
                    System.out.println(RED + "Fonda: pierdes un turno." + RESET);
                    skip[player] = 1;
                    break;

                // DOBLE TIRADA
                case 26:
                    System.out.println(YELLOW + "De dado a dado y tiro porque me ha tocado." + RESET);
                    repeat = true;
                    break;

                //POU
                case 31:
                    System.out.println(RED + "Pozo: pierdes dos turnos." + RESET);
                    skip[player] = 2;
                    break;

                // LABERINT
                case 42:
                    System.out.println(RED + "Laberinto: retrocedes a la casilla 39." + RESET);
                    positions[player] = 39;
                    break;

                // PRISIÓN
                case 52:
                    System.out.println(RED + "Prisión: pierdes tres turnos." + RESET);
                    skip[player] = 3;
                    break;

                // DADOS 4-5
                case 53:
                    System.out.println(YELLOW + "Dados 4-5: vuelves a tirar." + RESET);
                    repeat = true;
                    break;

                // LA MORT
                case 58:
                    System.out.println(RED + "La muerte: vuelves al inicio." + RESET);
                    positions[player] = 0;
                    break;

                // FINAL
                case 63:
                    positions[player] = numCells;
                    break;
            }
            // TIRADA EXTRA
            if (repeat) {
                int extra = rollDice(positions[player]);
                positions[player] += extra;
                // COMPROVACIÓ DE REBOT
                if (positions[player] > numCells) {
                    int excess = positions[player] - numCells;
                    positions[player] = numCells - excess;
                }
            }

        } while (repeat);
    }

    // SEGUENT OCA
    static int nextGoose(int current) {
        int[] goose = {5,9,14,18,23,27,32,36,41,45,50,54,59,63};
        for (int i = 0; i < goose.length - 1; i++) {
            if (goose[i] == current) {
                return goose[i + 1];
            }
        }
        return current;
    }

    // MÈTODES DE COMPROVACIÓ DE GUANYADOR
    static boolean checkWinner() {
        for (int i = 0; i < numPlayers; i++) {
            if (positions[i] >= numCells) {
                return true;
            }
        }
        return false;
    }
}
