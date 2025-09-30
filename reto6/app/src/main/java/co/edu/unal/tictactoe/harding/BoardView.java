package co.edu.unal.tictactoe.harding;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import co.edu.unal.tictactoe.R;

public class BoardView extends View {

    public static final int GRID_WIDTH = 6;
    private TicTacToeGame mGame;

    private Bitmap mHumanBitmap;
    private Bitmap mComputerBitmap;

    private Paint mPaint;

    public BoardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public void setGame(TicTacToeGame game) {
        mGame = game;
    }

    private void initialize() {
        mHumanBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.x_img);
        mComputerBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.o_img);

        mPaint = new Paint();
        mPaint.setColor(Color.GRAY);
        mPaint.setStrokeWidth(GRID_WIDTH);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth() - getPaddingLeft() - getPaddingRight();
        int height = getHeight() - getPaddingTop() - getPaddingBottom();

        int cellWidth = width / 3;
        int cellHeight = height / 3;

        // Escalar bitmaps dinámicamente para que se ajusten a las celdas
        Bitmap scaledHuman = Bitmap.createScaledBitmap(mHumanBitmap, cellWidth, cellHeight, true);
        Bitmap scaledComputer = Bitmap.createScaledBitmap(mComputerBitmap, cellWidth, cellHeight, true);

        // Dibuja líneas de la cuadrícula
        for (int i = 1; i < 3; i++) {
            // Vertical
            canvas.drawLine(i * cellWidth + getPaddingLeft(), getPaddingTop(),
                    i * cellWidth + getPaddingLeft(), height + getPaddingTop(), mPaint);
            // Horizontal
            canvas.drawLine(getPaddingLeft(), i * cellHeight + getPaddingTop(),
                    width + getPaddingLeft(), i * cellHeight + getPaddingTop(), mPaint);
        }

        // Dibuja X y O
        for (int i = 0; i < 9; i++) {
            int col = i % 3;
            int row = i / 3;

            int left = col * cellWidth + getPaddingLeft();
            int top = row * cellHeight + getPaddingTop();

            if (mGame != null) {
                char player = mGame.getBoardOccupant(i);
                if (player == TicTacToeGame.HUMAN_PLAYER) {
                    canvas.drawBitmap(scaledHuman, left, top, null);
                } else if (player == TicTacToeGame.COMPUTER_PLAYER) {
                    canvas.drawBitmap(scaledComputer, left, top, null);
                }
            }
        }
    }

    public int getBoardCellWidth() {
        return (getWidth() - getPaddingLeft() - getPaddingRight()) / 3;
    }

    public int getBoardCellHeight() {
        return (getHeight() - getPaddingTop() - getPaddingBottom()) / 3;
    }
}
