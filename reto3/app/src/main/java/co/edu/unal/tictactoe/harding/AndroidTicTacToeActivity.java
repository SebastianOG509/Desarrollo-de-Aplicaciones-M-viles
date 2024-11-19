package co.edu.unal.tictactoe.harding;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import co.edu.unal.tictactoe.R;

public class AndroidTicTacToeActivity extends Activity {

    private boolean mGameOver = false;
    private TicTacToeGame mGame;
    private Button mBoardButtons[]; // Arreglo de botones que representan el tablero
    private TextView mInfoTextView;  // Para mostrar información sobre el juego
    private Button mNewGameButton;   // Botón para iniciar un nuevo juego

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

        startNewGame();
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
