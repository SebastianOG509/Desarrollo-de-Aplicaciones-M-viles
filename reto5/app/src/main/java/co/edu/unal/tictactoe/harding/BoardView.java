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

        int width = getWidth();
        int height = getHeight();

        int cellWidth = width / 3;
        int cellHeight = height / 3;

        // Dibuja las líneas de la cuadrícula
        for (int i = 1; i < 3; i++) {
            canvas.drawLine(i * cellWidth, 0, i * cellWidth, height, mPaint);
            canvas.drawLine(0, i * cellHeight, width, i * cellHeight, mPaint);
        }

        // Dibuja las X y O
        for (int i = 0; i < 9; i++) {
            int col = i % 3;
            int row = i / 3;

            int left = col * cellWidth;
            int top = row * cellHeight;

            if (mGame != null) {
                char player = mGame.getBoardOccupant(i);
                if (player == TicTacToeGame.HUMAN_PLAYER) {
                    canvas.drawBitmap(mHumanBitmap, left, top, null);
                } else if (player == TicTacToeGame.COMPUTER_PLAYER) {
                    canvas.drawBitmap(mComputerBitmap, left, top, null);
                }
            }
        }
    }

    public int getBoardCellWidth() {
        return getWidth() / 3;
    }

    public int getBoardCellHeight() {
        return getHeight() / 3;
    }
}
