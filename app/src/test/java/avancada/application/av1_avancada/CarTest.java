package avancada.application.av1_avancada;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import android.graphics.Bitmap;
import android.graphics.Color;

import org.junit.Before;
import org.junit.Test;

public class CarTest {

    private Car car;

    // Método que será executado antes de cada teste
    @Before
    public void setUp() {
        // Criar um novo objeto Car
        car = new Car("CarroTeste", 100, 200, Color.RED, 50);
    }

    // Teste para verificar se o carro foi criado corretamente
    @Test
    public void testCarCreation() {
        // Verifica se os valores dos atributos são os esperados
        assertEquals("CarroTeste", car.getNome());
        assertEquals(100, car.getX());
        assertEquals(200, car.getY());
        assertEquals(Color.RED, car.getColor());
        assertEquals(50, car.getD());
        assertEquals(0, car.getDistance()); // A distância inicial deve ser 0
        assertEquals(3, car.getDirection()); // Direção inicial (direita)
        assertEquals(0, car.getPenalty()); // Penalidade inicial deve ser 0
        assertEquals(-1, car.getLaps()); // O número de voltas deve ser -1 no início
        assertNotNull(car.getSensor()); // Verifica se o sensor foi inicializado
        assertEquals(8, car.getSensor().size()); // Deve haver 8 sensores
    }

    @Test
    public void testCarFirstMove() {
        // Executa o primeiro movimento
        car.move();

        // Verifica se o carro se moveu para a direita (X incrementado em 1)
        assertEquals(101, car.getX());
        assertEquals(200, car.getY()); // Y deve permanecer o mesmo
        assertFalse(car.isFirstMove()); // Verifica se o firstMove foi alterado para false
    }

    @Test
    public void testSensorUpdateWithMock() {
        // Mock do Bitmap
        Bitmap mockBitmap = mock(Bitmap.class);

        // Simula o retorno da largura e altura do bitmap
        when(mockBitmap.getWidth()).thenReturn(500);
        when(mockBitmap.getHeight()).thenReturn(500);

        // Simula que todos os pixels da pista são brancos
        when(mockBitmap.getPixel(anyInt(), anyInt())).thenReturn(Color.WHITE);

        // Atualiza os sensores do carro
        car.updateSensors(mockBitmap);

        // Verifica se todos os sensores foram atualizados corretamente
        for (int i = 0; i < 8; i++) {
            assertEquals(car.getD(), (int) car.getSensor().get(i).intValue());
        }
    }

}
