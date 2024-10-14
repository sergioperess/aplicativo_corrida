package avancada.application.av1_avancada;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.util.HashMap;
import java.util.Map;

public class Car {
    private String nome;
    private int x;
    private int y;
    private int color; // Cor do carro
    private int d;
    private int penalty; // Penalidade por colisões
    private int direction; // Frente do carro
    private boolean firstMove; // Para garantir o primeiro movimento para a direita
    private Map<Integer, Integer> sensor; // Mapa de sensores
    private int distance;
    private int laps; // Número de voltas

    private static final int TRACK_COLOR = Color.WHITE; // Cor da pista

    public Car(String nome, int x, int y, int color, int d) {
        this.nome = nome;
        this.x = x;
        this.y = y;
        this.color = color;
        this.d = d; // Inicializa o raio do sensor
        this.distance = 0; // Inicializa a distância com 0
        this.direction = 3; // Iniciar apontando para a direita
        this.penalty = 0; // Inicializa o contador de penalidade
        this.laps = -1; // Inicializa o número de voltas
        this.sensor = new HashMap<>(); // Inicializa o mapa de sensores
        // Preenche o mapa de sensores com valores iniciais (distância máxima d)
        for (int i = 0; i < 8; i++) {
            sensor.put(i, d);
        }
    }

    public String getNome() {
        return nome;
    }
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getColor() {
        return color;
    }

    public int getDistance() {
        return distance;
    }

    public int getPenalty() {
        return penalty;
    }

    public int getLaps() {
        return laps;
    }

    public void setLaps(int laps) {
        this.laps = laps;
    }

    public Map<Integer, Integer> getSensorData() {
        return sensor;
    }

    // Método para atualizar os sensores a cada movimento
    public void updateSensors(Bitmap mutableBitmap) {
        for (int dir = 0; dir < 8; dir++) {
            int xTemp = x;
            int yTemp = y;
            double euclideanDistance = 0;

            while (euclideanDistance < d) {
                switch (dir) {
                    case 0: yTemp--; break; // Cima
                    case 1: yTemp++; break; // Baixo
                    case 2: xTemp--; break; // Esquerda
                    case 3: xTemp++; break; // Direita
                    case 4: xTemp--; yTemp--; break; // Diagonal superior esquerda
                    case 5: xTemp++; yTemp--; break; // Diagonal superior direita
                    case 6: xTemp--; yTemp++; break; // Diagonal inferior esquerda
                    case 7: xTemp++; yTemp++; break; // Diagonal inferior direita
                }

                if (xTemp < 0 || xTemp >= mutableBitmap.getWidth() || yTemp < 0 || yTemp >= mutableBitmap.getHeight()) {
                    euclideanDistance = d;
                    break;
                } else if (mutableBitmap.getPixel(xTemp, yTemp) != TRACK_COLOR) {
                    euclideanDistance = Math.sqrt(Math.pow(xTemp - x, 2) + Math.pow(yTemp - y, 2));
                    break;
                }

                euclideanDistance = Math.sqrt(Math.pow(xTemp - x, 2) + Math.pow(yTemp - y, 2));
            }

            sensor.put(dir, (int) Math.min(d, euclideanDistance)); // Atualiza o sensor
        }
    }

    // Movimento
    public void move(Bitmap mutableBitmap, Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(TRACK_COLOR);
        canvas.drawCircle(x, y, 10, paint); // Limpa a posição anterior

        updateSensors(mutableBitmap); // Atualiza os sensores

        // Verifica se é o primeiro movimento
        if (firstMove) {
            x++; // Movimenta para a direita
            firstMove = false;
        } else {
            // Determina as direções permitidas com base na frente do carro
            int[] forwardDirections;
            switch (direction) {
                case 0: forwardDirections = new int[]{0, 4, 5}; break; // Cima (0) -> frente e diagonais superiores
                case 1: forwardDirections = new int[]{1, 6, 7}; break; // Baixo (1) -> frente e diagonais inferiores
                case 2: forwardDirections = new int[]{2, 4, 6}; break; // Esquerda (2) -> esquerda e diagonais esquerdas
                case 3: forwardDirections = new int[]{3, 5, 7}; break; // Direita (3) -> direita e diagonais direitas
                case 4: forwardDirections = new int[]{4, 0, 2}; break; // Diagonal superior esquerda (4)
                case 5: forwardDirections = new int[]{5, 0, 3}; break; // Diagonal superior direita (5)
                case 6: forwardDirections = new int[]{6, 1, 2}; break; // Diagonal inferior esquerda (6)
                case 7: forwardDirections = new int[]{7, 1, 3}; break; // Diagonal inferior direita (7)
                default: forwardDirections = new int[]{3}; break; // Padrão para evitar erros
            }

            int selectedDirection = direction;
            double maxDistance = -1;

            // Seleciona a direção com mais espaço livre
            for (int dir : forwardDirections) {
                double sensorValue = sensor.get(dir);
                if (sensorValue > maxDistance) {
                    maxDistance = sensorValue;
                    selectedDirection = dir;
                }
            }

            // Move o carro na direção selecionada
            switch (selectedDirection) {
                case 0: y--; break; // Cima
                case 1: y++; break; // Baixo
                case 2: x--; break; // Esquerda
                case 3: x++; break; // Direita
                case 4: x--; y--; break; // Diagonal superior esquerda
                case 5: x++; y--; break; // Diagonal superior direita
                case 6: x--; y++; break; // Diagonal inferior esquerda
                case 7: x++; y++; break; // Diagonal inferior direita
            }

            // Atualiza a direção do carro
            direction = selectedDirection;
        }

        // Verifica colisão
        if (x < 0 || x >= mutableBitmap.getWidth() || y < 0 || y >= mutableBitmap.getHeight() ||
                mutableBitmap.getPixel(x, y) != TRACK_COLOR) {
            penalty++;
        }

        // Incrementa a distância e redesenha o carro
        distance++;
        paint.setColor(color);
        canvas.drawCircle(x, y, 10, paint); // Desenha o carro na nova posição

        // Desenha o círculo de detecção do sensor
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(1);
        canvas.drawCircle(x, y, d, paint);
    }

}
