package co.edu.unal.tictactoe.harding;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
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

    // MediaPlayer para los sonidos
    private MediaPlayer mPlayerMoveSound;
    private MediaPlayer mComputerMoveSound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        // Inicializa el objeto del juego
        mGame = new TicTacToeGame();

        // Vincula los elementos de la UI
        mBoardView = findViewById(R.id.board_view);
        mBoardView.setGame(mGame);
        mInfoTextView = findViewById(R.id.information);
        mMenuButton = findViewById(R.id.menu_button);

        // Inicializa los sonidos
        mPlayerMoveSound = MediaPlayer.create(this, R.raw.player_move_sound);
        mComputerMoveSound = MediaPlayer.create(this, R.raw.computer_move_sound);

        // Configura el listener táctil en el BoardView
        mBoardView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int col = (int) event.getX() / mBoardView.getBoardCellWidth();
                int row = (int) event.getY() / mBoardView.getBoardCellHeight();
                int pos = row * 3 + col;

                if (!mGameOver && mGame.setMove(TicTacToeGame.HUMAN_PLAYER, pos)) {
                    mBoardView.invalidate(); // Redibuja el tablero
                    mPlayerMoveSound.start(); // Reproduce el sonido del jugador

                    int winner = mGame.checkForWinner();
                    if (winner == 0) {
                        mInfoTextView.setText("Turno de Android.");
                        mBoardView.postDelayed(() -> {
                            int move = mGame.getComputerMove();
                            mGame.setMove(TicTacToeGame.COMPUTER_PLAYER, move);
                            mBoardView.invalidate();
                            mComputerMoveSound.start(); // Reproduce el sonido de la máquina
                            checkForWinner();
                        }, 1000);
                    } else {
                        checkForWinner();
                    }
                }
                return false;
            }
        });

        // Configura el botón de menú
        mMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(AndroidTicTacToeActivity.this, v);
                popupMenu.getMenuInflater().inflate(R.menu.options_menu, popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(item -> {
                    int id = item.getItemId();
                    if (id == R.id.new_game) {
                        startNewGame();  // Reiniciar el juego
                        return true;
                    } else if (id == R.id.ai_difficulty) {
                        showDialog(DIALOG_DIFFICULTY_ID);  // Mostrar el diálogo de dificultad
                        return true;
                    } else if (id == R.id.quit) {
                        showDialog(DIALOG_QUIT_ID);  // Mostrar el diálogo de salida
                        return true;
                    }
                    return false;
                });

                popupMenu.show();
            }
        });

        startNewGame();
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
            mGameOver = true;
        } else if (winner == 2) {
            mInfoTextView.setText("¡Ganaste! :D");
            mGameOver = true;
        } else if (winner == 3) {
            mInfoTextView.setText("¡Perdiste! D:");
            mGameOver = true;
        }
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        Dialog dialog = null;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        switch (id) {
            case DIALOG_DIFFICULTY_ID:
                builder.setTitle(R.string.difficulty_choose);
                final CharSequence[] levels = {
                        getResources().getString(R.string.difficulty_easy),
                        getResources().getString(R.string.difficulty_harder),
                        getResources().getString(R.string.difficulty_expert)
                };
                builder.setSingleChoiceItems(levels, 0,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int item) {
                                dialog.dismiss();
                                mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.values()[item]);
                                Toast.makeText(getApplicationContext(),
                                        levels[item], Toast.LENGTH_SHORT).show();
                            }
                        });
                dialog = builder.create();
                break;

            case DIALOG_QUIT_ID:
                builder.setMessage(R.string.quit_question)
                        .setCancelable(false)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                finish();
                            }
                        })
                        .setNegativeButton(R.string.no, null);
                dialog = builder.create();
                break;
        }
        return dialog;
    }

    @Override
    protected void onDestroy() {
        // Libera los recursos de MediaPlayer cuando la actividad se destruya
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
