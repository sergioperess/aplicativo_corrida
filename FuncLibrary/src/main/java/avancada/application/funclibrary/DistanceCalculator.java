package avancada.application.funclibrary;

public class DistanceCalculator {

    /**
     * Calcula a distância euclidiana entre dois pontos (x, y) e (xTemp, yTemp).
     *
     * @param x      Coordenada x do primeiro ponto.
     * @param y      Coordenada y do primeiro ponto.
     * @param xTemp  Coordenada x do segundo ponto.
     * @param yTemp  Coordenada y do segundo ponto.
     * @return       A distância euclidiana entre os pontos.
     */
    public static double calculateEuclideanDistance(double x, double y, double xTemp, double yTemp) {
        return Math.sqrt(Math.pow(xTemp - x, 2) + Math.pow(yTemp - y, 2));
    }
}
