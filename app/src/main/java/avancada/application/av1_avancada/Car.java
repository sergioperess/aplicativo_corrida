package avancada.application.av1_avancada;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.util.Map;
import java.util.Random;

public class Car {
    private String name;
    private int x;
    private int y;
    private int color;
    private int fuelTank;
    private double speed;
    private int laps;
    private int distance;
    private int penalty;
    private Map<String, Integer> sensor;

    public Car(String name, int x, int y, int color) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    // Método para verificar se o pixel é branco
    private boolean isWhitePixel(int x, int y, Bitmap bitmap) {
        int pixel = bitmap.getPixel(x, y);
        return pixel == Color.WHITE;
    }

    // Método para mover o carro para um pixel branco adjacente
    public void moveCarToWhitePixel(Bitmap mutableBitmap, Canvas canvas) {
        Random random = new Random();

        int newX = this.x;
        int newY = this.y;

        // Gerar uma direção aleatória: 0 = cima, 1 = baixo, 2 = esquerda, 3 = direita
        int direction = random.nextInt(4);
        switch (direction) {
            case 0: // Cima
                newY = Math.max(0, this.y - 1);
                break;
            case 1: // Baixo
                newY = Math.min(mutableBitmap.getHeight() - 1, this.y + 1);
                break;
            case 2: // Esquerda
                newX = Math.max(0, this.x - 1);
                break;
            case 3: // Direita
                newX = Math.min(mutableBitmap.getWidth() - 1, this.x + 1);
                break;
        }

        // Verificar se o novo pixel é branco
        if (isWhitePixel(newX, newY, mutableBitmap)) {
            // Atualizar a posição do carro
            this.x = newX;
            this.y = newY;

            // Desenhar o carro na nova posição com sua cor
            Paint paint = new Paint();
            paint.setColor(this.color);
            canvas.drawCircle(newX, newY, 10, paint); // Desenhar carro na nova posição
        }
    }

}
