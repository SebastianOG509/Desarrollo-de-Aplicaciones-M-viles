package co.edu.unal.tictactoe.harding;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import co.edu.unal.tictactoe.R;

public class AndroidTicTacToeActivity extends Activity {

    private int selected = 0;
    static final int DIALOG_DIFFICULTY_ID = 0;
    static final int DIALOG_QUIT_ID = 1;

    private boolean mGameOver = false;
    private TicTacToeGame mGame;
    private Button mBoardButtons[]; // Arreglo de botones que representan el tablero
    private TextView mInfoTextView;  // Para mostrar información sobre el juego
    private Button mNewGameButton;   // Botón para iniciar un nuevo juego
    private Button menuButton;       // Botón para mostrar el menú

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
                builder.setSingleChoiceItems(levels, selected,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int item) {
                                dialog.dismiss();
                                // Ajustar el nivel de dificultad
                                mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.values()[item]);
                                Toast.makeText(getApplicationContext(),
                                        levels[item], Toast.LENGTH_SHORT).show();
                            }
                        });
                dialog = builder.create();
                break;

            case DIALOG_QUIT_ID:
                // Crear el diálogo de confirmación para salir
                builder.setMessage(R.string.quit_question)
                        .setCancelable(false)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // Termina la actividad
                                AndroidTicTacToeActivity.this.finish();
                            }
                        })
                        .setNegativeButton(R.string.no, null);
                dialog = builder.create();
                break;
        }
        return dialog;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        // Inicializa el arreglo de botones del tablero
        mBoardButtons = new Button[9];
        mBoardButtons[0] = findViewById(R.id.one);
        mBoardButtons[1] = findViewById(R.id.two);
        mBoardButtons[2] = findViewById(R.id.three);
        mBoardButtons[3] = findViewById(R.id.four);
        mBoardButtons[4] = findViewById(R.id.five);
        mBoardButtons[5] = findViewById(R.id.six);
        mBoardButtons[6] = findViewById(R.id.seven);
        mBoardButtons[7] = findViewById(R.id.eight);
        mBoardButtons[8] = findViewById(R.id.nine);

        // Inicializa el TextView que muestra información
        mInfoTextView = findViewById(R.id.information);

        // Inicializa el botón de "New Game" y lo oculta inicialmente
        mNewGameButton = findViewById(R.id.new_game_button);
        mNewGameButton.setVisibility(View.GONE); // Botón oculto al principio

        // Inicializa el objeto del juego
        mGame = new TicTacToeGame();

        // Inicializa el botón de menú
        menuButton = findViewById(R.id.menu_button);
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(AndroidTicTacToeActivity.this, v);
                MenuInflater inflater = popupMenu.getMenuInflater();
                inflater.inflate(R.menu.options_menu, popupMenu.getMenu()); // Infla el menú

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        return onOptionsItemSelected(item); // Llama al método para manejar la opción seleccionada
                    }
                });

                popupMenu.show();  // Muestra el menú
            }
        });

        startNewGame();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu); // Inflar el archivo de menú
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
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
        return super.onOptionsItemSelected(item);
    }

    private void startNewGame() {
        // Limpia el estado interno del tablero en el objeto TicTacToeGame
        mGame.clearBoard();

        // Restablece los botones y asigna un OnClickListener a cada uno
        for (int i = 0; i < mBoardButtons.length; i++) {
            mBoardButtons[i].setText(""); // Limpia el texto de cada botón
            mBoardButtons[i].setEnabled(true); // Habilita el botón
            mBoardButtons[i].setOnClickListener(new ButtonClickListener(i)); // Asigna el OnClickListener
        }

        // Restablece el texto de información
        mInfoTextView.setText("Vas primero.");
        mGameOver = false;

        // Oculta el botón de "New Game" cuando se inicia un nuevo juego
        mNewGameButton.setVisibility(View.GONE);
    }

    // Maneja los clics en los botones del tablero de juego
    private class ButtonClickListener implements View.OnClickListener {
        private int location;

        public ButtonClickListener(int location) {
            this.location = location;
        }

        @Override
        public void onClick(View view) {
            if (mGameOver) {
                return; // No hace nada si el juego ya ha terminado
            }

            if (mBoardButtons[location].isEnabled()) {
                setMove(TicTacToeGame.HUMAN_PLAYER, location);

                // Verifica si hay un ganador
                int winner = mGame.checkForWinner();
                if (winner == 0) {
                    // Si no hay ganador, es el turno de la computadora
                    mInfoTextView.setText("It's Android's turn.");
                    int move = mGame.getComputerMove(); // Movimiento de la computadora
                    setMove(TicTacToeGame.COMPUTER_PLAYER, move);
                    winner = mGame.checkForWinner(); // Verifica si la computadora ganó
                }

                // Actualiza el estado del juego
                if (winner == 0) {
                    mInfoTextView.setText("Es tu turno.");
                } else {
                    mGameOver = true; // El juego ha terminado
                    if (winner == 1) {
                        mInfoTextView.setText("Empate ._.");
                    } else if (winner == 2) {
                        mInfoTextView.setText("GANASTE! :D");
                    } else {
                        mInfoTextView.setText("PERDISTE! :(");
                    }

                    // Muestra el botón de "New Game" al final de la partida
                    mNewGameButton.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    // Establece un movimiento del jugador en el tablero
    private void setMove(char player, int location) {
        mGame.setMove(player, location);
        mBoardButtons[location].setEnabled(false);
        mBoardButtons[location].setText(String.valueOf(player));
        if (player == TicTacToeGame.HUMAN_PLAYER) {
            mBoardButtons[location].setTextColor(Color.rgb(0, 200, 0));
        } else {
            mBoardButtons[location].setTextColor(Color.rgb(200, 0, 0));
        }
    }

    // Método que se ejecuta al hacer clic en el botón "New Game"
    public void onNewGameClicked(View view) {
        startNewGame();  // Reinicia el juego
    }
}
