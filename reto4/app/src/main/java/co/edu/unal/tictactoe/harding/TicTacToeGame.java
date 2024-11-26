package co.edu.unal.tictactoe.harding;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TicTacToeGame {

    public enum DifficultyLevel {
        EASY,
        HARDER,
        EXPERT
    }

    private DifficultyLevel mDifficultyLevel = DifficultyLevel.EASY;

    public DifficultyLevel getDifficultyLevel() {
        return mDifficultyLevel;
    }

    public void setDifficultyLevel(DifficultyLevel difficultyLevel) {
        mDifficultyLevel = difficultyLevel;
    }

    private char mBoard[];
    private static final int BOARD_SIZE = 9;

    public static final char HUMAN_PLAYER = 'X';
    public static final char COMPUTER_PLAYER = 'O';
    public static final char OPEN_SPOT = ' ';

    private Random mRand;

    public TicTacToeGame() {
        mBoard = new char[BOARD_SIZE];
        mRand = new Random();
        clearBoard();
    }

    public void clearBoard() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            mBoard[i] = OPEN_SPOT;
        }
    }

    public void setMove(char player, int location) {
        if (location >= 0 && location < BOARD_SIZE && mBoard[location] == OPEN_SPOT) {
            mBoard[location] = player;
        }
    }

    public int getComputerMove() {
        int move = -1;
        if (mDifficultyLevel == DifficultyLevel.EASY)
            move = getRandomMove();
        else if (mDifficultyLevel == DifficultyLevel.HARDER) {
            move = getWinningMove();
            if (move == -1)
                move = getRandomMove();
        }
        else if (mDifficultyLevel == DifficultyLevel.EXPERT) {
            move = getWinningMove();
            if (move == -1)
                move = getBlockingMove();
            if (move == -1)
                move = getRandomMove();
        }
        return move;
    }

    private int getRandomMove() {
        List<Integer> availableMoves = new ArrayList<>();
        for (int i = 0; i < mBoard.length; i++) {
            if (mBoard[i] == OPEN_SPOT) {
                availableMoves.add(i);
            }
        }
        if (!availableMoves.isEmpty()) {
            Random rand = new Random();
            return availableMoves.get(rand.nextInt(availableMoves.size()));
        }
        return -1;
    }

    private int getBlockingMove() {
        for (int i = 0; i < mBoard.length; i++) {
            if (mBoard[i] == OPEN_SPOT) {
                mBoard[i] = HUMAN_PLAYER;
                if (checkForWinner() == 2) {
                    mBoard[i] = OPEN_SPOT;
                    return i;
                }
                mBoard[i] = OPEN_SPOT;
            }
        }
        return -1;
    }

    private int getWinningMove() {
        for (int i = 0; i < mBoard.length; i++) {
            if (mBoard[i] == OPEN_SPOT) {
                mBoard[i] = COMPUTER_PLAYER;
                if (checkForWinner() == 3) {
                    mBoard[i] = OPEN_SPOT;
                    return i;
                }
                mBoard[i] = OPEN_SPOT;
            }
        }
        return -1;
    }

    public int checkForWinner() {
        for (int i = 0; i <= 6; i += 3) {
            if (mBoard[i] == HUMAN_PLAYER &&
                    mBoard[i + 1] == HUMAN_PLAYER &&
                    mBoard[i + 2] == HUMAN_PLAYER) {
                return 2;
            }
            if (mBoard[i] == COMPUTER_PLAYER &&
                    mBoard[i + 1] == COMPUTER_PLAYER &&
                    mBoard[i + 2] == COMPUTER_PLAYER) {
                return 3;
            }
        }

        for (int i = 0; i <= 2; i++) {
            if (mBoard[i] == HUMAN_PLAYER &&
                    mBoard[i + 3] == HUMAN_PLAYER &&
                    mBoard[i + 6] == HUMAN_PLAYER) {
                return 2;
            }
            if (mBoard[i] == COMPUTER_PLAYER &&
                    mBoard[i + 3] == COMPUTER_PLAYER &&
                    mBoard[i + 6] == COMPUTER_PLAYER) {
                return 3;
            }
        }

        if ((mBoard[0] == HUMAN_PLAYER &&
                mBoard[4] == HUMAN_PLAYER &&
                mBoard[8] == HUMAN_PLAYER) ||
                (mBoard[2] == HUMAN_PLAYER &&
                        mBoard[4] == HUMAN_PLAYER &&
                        mBoard[6] == HUMAN_PLAYER)) {
            return 2;
        }
        if ((mBoard[0] == COMPUTER_PLAYER &&
                mBoard[4] == COMPUTER_PLAYER &&
                mBoard[8] == COMPUTER_PLAYER) ||
                (mBoard[2] == COMPUTER_PLAYER &&
                        mBoard[4] == COMPUTER_PLAYER &&
                        mBoard[6] == COMPUTER_PLAYER)) {
            return 3;
        }

        for (int i = 0; i < BOARD_SIZE; i++) {
            if (mBoard[i] == OPEN_SPOT) {
                return 0;
            }
        }

        return 1;
    }
}
