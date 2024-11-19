package co.edu.unal.tictactoe.harding;

import java.util.Random;

public class TicTacToeGame {

    private char mBoard[]; // Representa el tablero del juego
    private static final int BOARD_SIZE = 9; // Tamaño del tablero

    public static final char HUMAN_PLAYER = 'X'; // Jugador humano
    public static final char COMPUTER_PLAYER = 'O'; // Jugador computadora
    public static final char OPEN_SPOT = ' '; // Espacio vacío

    private Random mRand; // Generador de números aleatorios

    /**
     * Constructor: inicializa el tablero y el generador aleatorio.
     */
    public TicTacToeGame() {
        mBoard = new char[BOARD_SIZE];
        mRand = new Random();
        clearBoard(); // Limpia el tablero al inicializar el juego
    }



    /**
     * Limpia el tablero configurando todas las posiciones como OPEN_SPOT.
     */
    public void clearBoard() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            mBoard[i] = OPEN_SPOT;
        }
    }

    /**
     * Establece el movimiento de un jugador en una ubicación específica.
     *
     * @param player   El jugador (HUMAN_PLAYER o COMPUTER_PLAYER).
     * @param location La posición (0-8) donde se coloca la marca.
     */
    public void setMove(char player, int location) {
        if (location >= 0 && location < BOARD_SIZE && mBoard[location] == OPEN_SPOT) {
            mBoard[location] = player;
        }
    }

    /**
     * Devuelve el mejor movimiento para la computadora.
     *
     * @return La posición (0-8) del mejor movimiento.
     */
    public int getComputerMove() {
        int move;

        // Verifica si puede ganar en la siguiente jugada
        for (int i = 0; i < BOARD_SIZE; i++) {
            if (mBoard[i] == OPEN_SPOT) {
                mBoard[i] = COMPUTER_PLAYER;
                if (checkForWinner() == 3) {
                    mBoard[i] = OPEN_SPOT; // Restaura el tablero
                    return i;
                }
                mBoard[i] = OPEN_SPOT;
            }
        }

        // Bloquea al humano si está por ganar
        for (int i = 0; i < BOARD_SIZE; i++) {
            if (mBoard[i] == OPEN_SPOT) {
                mBoard[i] = HUMAN_PLAYER;
                if (checkForWinner() == 2) {
                    mBoard[i] = OPEN_SPOT; // Restaura el tablero
                    return i;
                }
                mBoard[i] = OPEN_SPOT;
            }
        }

        // Selecciona una posición aleatoria válida
        do {
            move = mRand.nextInt(BOARD_SIZE);
        } while (mBoard[move] != OPEN_SPOT);

        return move;
    }

    /**
     * Verifica el estado del juego y determina el ganador.
     *
     * @return 0 = Sin ganador ni empate, 1 = Empate, 2 = Gana X, 3 = Gana O.
     */
    public int checkForWinner() {
        // Comprobar victorias horizontales
        for (int i = 0; i <= 6; i += 3) {
            if (mBoard[i] == HUMAN_PLAYER &&
                    mBoard[i + 1] == HUMAN_PLAYER &&
                    mBoard[i + 2] == HUMAN_PLAYER) {
                return 2; // Gana X
            }
            if (mBoard[i] == COMPUTER_PLAYER &&
                    mBoard[i + 1] == COMPUTER_PLAYER &&
                    mBoard[i + 2] == COMPUTER_PLAYER) {
                return 3; // Gana O
            }
        }

        // Comprobar victorias verticales
        for (int i = 0; i <= 2; i++) {
            if (mBoard[i] == HUMAN_PLAYER &&
                    mBoard[i + 3] == HUMAN_PLAYER &&
                    mBoard[i + 6] == HUMAN_PLAYER) {
                return 2; // Gana X
            }
            if (mBoard[i] == COMPUTER_PLAYER &&
                    mBoard[i + 3] == COMPUTER_PLAYER &&
                    mBoard[i + 6] == COMPUTER_PLAYER) {
                return 3; // Gana O
            }
        }

        // Comprobar victorias diagonales
        if ((mBoard[0] == HUMAN_PLAYER &&
                mBoard[4] == HUMAN_PLAYER &&
                mBoard[8] == HUMAN_PLAYER) ||
                (mBoard[2] == HUMAN_PLAYER &&
                        mBoard[4] == HUMAN_PLAYER &&
                        mBoard[6] == HUMAN_PLAYER)) {
            return 2; // Gana X
        }
        if ((mBoard[0] == COMPUTER_PLAYER &&
                mBoard[4] == COMPUTER_PLAYER &&
                mBoard[8] == COMPUTER_PLAYER) ||
                (mBoard[2] == COMPUTER_PLAYER &&
                        mBoard[4] == COMPUTER_PLAYER &&
                        mBoard[6] == COMPUTER_PLAYER)) {
            return 3; // Gana O
        }

        // Comprobar si aún hay movimientos disponibles
        for (int i = 0; i < BOARD_SIZE; i++) {
            if (mBoard[i] == OPEN_SPOT) {
                return 0; // No hay ganador todavía
            }
        }

        return 1; // Empate
    }
}


