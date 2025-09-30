package co.edu.unal.tictactoe.harding;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import co.edu.unal.tictactoe.R;

public class AndroidTicTacToeActivity extends Activity {

    private static final int DIALOG_DIFFICULTY_ID = 0;
    private static final int DIALOG_QUIT_ID = 1;

    private TicTacToeGame mGame;
    private BoardView mBoardView;
    private TextView mInfoTextView;
    private boolean mGameOver = false;
    private Button mMenuButton;

    // Puntajes
    private int mHumanWins = 0;
    private int mComputerWins = 0;
    private int mTies = 0;

    // SharedPreferences
    private SharedPreferences mPrefs;

    // MediaPlayer
    private MediaPlayer mPlayerMoveSound;
    private MediaPlayer mComputerMoveSound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        // SharedPreferences
        mPrefs = getSharedPreferences("ttt_prefs", MODE_PRIVATE);
        mHumanWins = mPrefs.getInt("mHumanWins", 0);
        mComputerWins = mPrefs.getInt("mComputerWins", 0);
        mTies = mPrefs.getInt("mTies", 0);

        // Inicializa juego
        mGame = new TicTacToeGame();

        // Vincula UI
        mBoardView = findViewById(R.id.board_view);
        mBoardView.setGame(mGame);
        mInfoTextView = findViewById(R.id.information);
        mMenuButton = findViewById(R.id.menu_button);

        // Inicializa sonidos
        mPlayerMoveSound = MediaPlayer.create(this, R.raw.player_move_sound);
        mComputerMoveSound = MediaPlayer.create(this, R.raw.computer_move_sound);

        // Listener táctil
        mBoardView.setOnTouchListener((v, event) -> {
            int col = (int) event.getX() / mBoardView.getBoardCellWidth();
            int row = (int) event.getY() / mBoardView.getBoardCellHeight();
            int pos = row * 3 + col;

            if (!mGameOver && mGame.setMove(TicTacToeGame.HUMAN_PLAYER, pos)) {
                mBoardView.invalidate();
                mPlayerMoveSound.start();

                int winner = mGame.checkForWinner();
                if (winner == 0) {
                    mInfoTextView.setText("Turno de Android.");
                    mBoardView.postDelayed(() -> {
                        if (!mGameOver) {
                            int move = mGame.getComputerMove();
                            mGame.setMove(TicTacToeGame.COMPUTER_PLAYER, move);
                            mBoardView.invalidate();
                            mComputerMoveSound.start();
                            checkForWinner();
                        }
                    }, 1000);
                } else {
                    checkForWinner();
                }
            }
            return false;
        });

        // Botón de menú
        mMenuButton.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(AndroidTicTacToeActivity.this, v);
            popupMenu.getMenuInflater().inflate(R.menu.options_menu, popupMenu.getMenu());

            popupMenu.setOnMenuItemClickListener(item -> {
                int id = item.getItemId();
                if (id == R.id.new_game) {
                    startNewGame();
                    return true;
                } else if (id == R.id.ai_difficulty) {
                    showDialog(DIALOG_DIFFICULTY_ID);
                    return true;
                } else if (id == R.id.reset_scores) {
                    mHumanWins = mComputerWins = mTies = 0;
                    displayScores();
                    return true;
                } else if (id == R.id.quit) {
                    showDialog(DIALOG_QUIT_ID);
                    return true;
                }
                return false;
            });
            popupMenu.show();
        });

        // Restaurar estado
        if (savedInstanceState != null) {
            mGame.setBoardState(savedInstanceState.getCharArray("board"));
            mGameOver = savedInstanceState.getBoolean("mGameOver");
            mInfoTextView.setText(savedInstanceState.getCharSequence("info"));
            mGame.setDifficultyLevel((TicTacToeGame.DifficultyLevel) savedInstanceState.getSerializable("difficulty"));
        } else {
            startNewGame();
        }

        displayScores();

        // Turno computadora tras rotación
        if (!mGameOver && mGame.isComputerTurn()) {
            mBoardView.postDelayed(() -> {
                int move = mGame.getComputerMove();
                mGame.setMove(TicTacToeGame.COMPUTER_PLAYER, move);
                mBoardView.invalidate();
                mComputerMoveSound.start();
                checkForWinner();
            }, 500);
        }
    }

    private void startNewGame() {
        mGame.clearBoard();
        mBoardView.invalidate();
        mInfoTextView.setText("Vas primero.");
        mGameOver = false;
    }

    private void checkForWinner() {
        int winner = mGame.checkForWinner();
        if (winner == 1) {
            mInfoTextView.setText("Empate ._.");
            mTies++;
            mGameOver = true;
        } else if (winner == 2) {
            mInfoTextView.setText("¡Ganaste! :D");
            mHumanWins++;
            mGameOver = true;
        } else if (winner == 3) {
            mInfoTextView.setText("¡Perdiste! D:");
            mComputerWins++;
            mGameOver = true;
        }
        displayScores();
    }

    private void displayScores() {
        TextView humanScore = findViewById(R.id.human_score);
        TextView computerScore = findViewById(R.id.computer_score);
        TextView tieScore = findViewById(R.id.tie_score);

        humanScore.setText(Integer.toString(mHumanWins));
        computerScore.setText(Integer.toString(mComputerWins));
        tieScore.setText(Integer.toString(mTies));
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        Dialog dialog = null;

        switch (id) {
            case DIALOG_DIFFICULTY_ID:
                builder.setTitle(R.string.difficulty_choose);
                final CharSequence[] levels = {
                        getResources().getString(R.string.difficulty_easy),
                        getResources().getString(R.string.difficulty_harder),
                        getResources().getString(R.string.difficulty_expert)
                };
                builder.setSingleChoiceItems(levels, mGame.getDifficultyLevel().ordinal(), (dialog1, item) -> {
                    dialog1.dismiss();
                    mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.values()[item]);
                    Toast.makeText(getApplicationContext(), levels[item], Toast.LENGTH_SHORT).show();
                });
                dialog = builder.create();
                break;

            case DIALOG_QUIT_ID:
                builder.setMessage(R.string.quit_question)
                        .setCancelable(false)
                        .setPositiveButton(R.string.yes, (dialog12, id1) -> finish())
                        .setNegativeButton(R.string.no, null);
                dialog = builder.create();
                break;
        }
        return dialog;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putCharArray("board", mGame.getBoardState());
        outState.putBoolean("mGameOver", mGameOver);
        outState.putCharSequence("info", mInfoTextView.getText());
        outState.putSerializable("difficulty", mGame.getDifficultyLevel());
    }

    @Override
    protected void onStop() {
        super.onStop();
        SharedPreferences.Editor ed = mPrefs.edit();
        ed.putInt("mHumanWins", mHumanWins);
        ed.putInt("mComputerWins", mComputerWins);
        ed.putInt("mTies", mTies);
        ed.commit();
    }

    @Override
    protected void onDestroy() {
        if (mPlayerMoveSound != null) {
            mPlayerMoveSound.release();
            mPlayerMoveSound = null;
        }
        if (mComputerMoveSound != null) {
            mComputerMoveSound.release();
            mComputerMoveSound = null;
        }
        super.onDestroy();
    }
}
