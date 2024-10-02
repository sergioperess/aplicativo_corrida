package avancada.application.av1_avancada;

import java.util.Map;

public class Car {
    private String name;
    private int x;
    private int y;
    private int fuelTank;
    private double speed;
    private int laps;
    private int distance;
    private int penalty;
    private Map<String, Integer> sensor;

    public Car(String name) {
        this.name = name;
    }

    // Método para atualizar a penalidade
    public void penalidade(){
        penalty++;
    }

    // Método para atualizar a posição do carro
    public void attPosicao(int x1, int y1){

        distance++;
    }

    public String getName() {
        return name;
    }
}
