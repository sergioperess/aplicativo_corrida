package avancada.application.av1_avancada;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private boolean isPaused = false;
    private boolean isStarted = false;
    private Handler handler;
    private Runnable runnable;
    private Button pauseButton;
    private ImageView imageView;
    private Bitmap mutableBitmap;
    private Bitmap originalBitmap;
    private Canvas canvas;
    private Paint paint;
    private EditText campoQuantidadeCarros;
    // Lista para armazenar os pixels da linha de chegada (coordenadas x, y)
    private final List<int[]> linhadeChegada = new ArrayList<>();

    private final List<Car> carList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        // Conectar componentes ao layout
        campoQuantidadeCarros = findViewById(R.id.campoQuantidadeCarros);
        Button botaoStart = findViewById(R.id.botaoStart);

        // Carregar a imagem e modificar o pixel
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.pista);
        
        // Criar um objeto Paint para definir a cor e o estilo do desenho
        paint = new Paint();
        //paint.setColor(getRandomColor()); // Definir a cor para vermelho

        // Desenhar um pixel na coordenada (250, 75)
        //canvas.drawCircle(656, 198, 10, paint);

        imageView = findViewById(R.id.myImageView);

        // Configurar o comportamento do botão Start
        botaoStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!isStarted) {
                    // Verificar se o campo foi preenchido
                    String quantidadeTexto = campoQuantidadeCarros.getText().toString();

                    int centroX = 656; // Posição inicial do pixel
                    int centroY = 656;
                    int raio = 458; // Raio do círculo
                    double anguloIncremento = 5; // Pequeno incremento do ângulo para espaçamento menor (5 graus)


                    if (!quantidadeTexto.isEmpty()) {

                        isStarted = true;
                        // Limpar a lista de carros e o canvas
                        carList.clear();

                        mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
                        // Criar um Canvas associado ao Bitmap mutável
                        canvas = new Canvas(mutableBitmap);

                        int width = mutableBitmap.getWidth();
                        int height = mutableBitmap.getHeight();
                        Log.d("BitmapSize", "Largura: " + width + " Altura: " + height);

                        int quantidadeCarros = Integer.parseInt(quantidadeTexto);

                        // Adiciona os carros criados e os distribui em um formato circular (anti-horário com pouco espaçamento)
                        double anguloAtual = 90; // Começa em 0 graus

                        // Adiciona os carros criados
                        for (int i = 0; i < quantidadeCarros; i++) {

                            // Gerar uma cor aleatória para o carro
                            int carColor = getRandomColor();

                            // Calcular as coordenadas (x, y) com base no ângulo e no raio
                            int x = (int) (centroX + raio * Math.cos(Math.toRadians(anguloAtual)));
                            int y = (int) (centroY - raio * Math.sin(Math.toRadians(anguloAtual))); // Subtraindo para fazer anti-horário

                            // Desenhar o círculo correspondente a esse carro
                            //paint.setColor(getRandomColor()); // Definir uma cor aleatória para cada carro
                            //canvas.drawCircle(x, y, 10, paint);

                            Car car = new Car("Carro " + i, x, y, carColor);
                            carList.add(car);

                            // Desenhar o círculo correspondente a esse carro usando sua cor inicial
                            Paint paint = new Paint();
                            paint.setColor(carColor);
                            canvas.drawCircle(x, y, 10, paint);

                            // Desenhar o círculo correspondente a esse carro (pixel)
                            //paint.setColor(getRandomColor()); // Definir uma cor aleatória para cada carro
                            //canvas.drawCircle(x, y, 10, paint); // Desenhar um pequeno círculo (pixel) em (x, y)

                            // Atualizar o ângulo para o próximo pixel, decrementando para mover no sentido anti-horário
                            anguloAtual += anguloIncremento; // Diminui o ângulo para mover anti-horário
                        }

                        if (quantidadeCarros > 0) {

                            imageView.setImageBitmap(mutableBitmap);

                            // Inicializando o Handler e o Runnable
                            handler = new Handler();
                            runnable = new Runnable() {

                                @Override
                                public void run() {
                                    if (!isPaused) {

                                        // Para cada carro na lista, tenta mover para um pixel branco
                                        for (Car car : carList) {
                                            car.moveCarToWhitePixel(mutableBitmap, canvas);
                                            Log.d(car.getName(), "X: " + car.getX() + " y: " + car.getY());
                                        }

                                        // Redesenhar a tela com as novas posições dos carros
                                        imageView.setImageBitmap(mutableBitmap);

                                        // Sua tarefa em execução (simulada por um Toast aqui)
                                        Toast.makeText(MainActivity.this, "Tarefa em execução", Toast.LENGTH_SHORT).show();

                                        // Repetir a tarefa a cada 2 segundos
                                        handler.postDelayed(this, 200);
                                    }
                                }
                            };

                            // Iniciar a tarefa
                            handler.post(runnable);

                            // Iniciar a próxima ação (por exemplo, iniciar uma corrida)
                            Toast.makeText(MainActivity.this, "Corrida iniciada com " + quantidadeCarros + " carros", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MainActivity.this, "Por favor, insira um número válido de carros", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // Mostrar mensagem de erro se o campo estiver vazio
                        Toast.makeText(MainActivity.this, "Por favor, insira a quantidade de carros", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    // Mostrar mensagem de erro se o campo estiver vazio
                    Toast.makeText(MainActivity.this, "Programa já esta em execução", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Inicializando o botão de pausa
        pauseButton = findViewById(R.id.botaoPause);

        // Definir a lógica do botão de pausa
        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isStarted) {
                    if (!isPaused) {
                        isPaused = true;
                        pauseButton.setText("Retomar");
                        Toast.makeText(MainActivity.this, "Pausado", Toast.LENGTH_SHORT).show();
                    } else {
                        isPaused = false;
                        pauseButton.setText("Pausar");
                        handler.post(runnable);  // Retomar a tarefa
                        Toast.makeText(MainActivity.this, "Retomado", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Iniciar atividade", Toast.LENGTH_SHORT).show();
                }
            }
        });


        // Referenciar o botão de finalizar
        Button finishButton = findViewById(R.id.botaoFinish);

        // Definir a ação do botão de finalizar
        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finalizar a atividade
                if (!isPaused) {

                    imageView.setImageResource(R.drawable.pista);
                    isStarted = false;
                    // Parar completamente a tarefa
                    if (handler != null && runnable != null) {
                        handler.removeCallbacks(runnable); // Remove a tarefa
                        Toast.makeText(MainActivity.this, "Tarefa finalizada", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Retomar atividade", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    // Método para gerar uma cor aleatória que não seja branca
    private int getRandomColor() {
        Random random = new Random();
        int color;
        do {
            color = Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256));
        } while (color == Color.WHITE); // Garantir que a cor não seja branca
        return color;
    }

    // Método para verificar se o pixel é branco
    private boolean isWhitePixel(int x, int y, Bitmap bitmap) {
        int pixelColor = bitmap.getPixel(x, y);
        return pixelColor == Color.WHITE;
    }

    // Método para restaurar a imagem original (remover os pontos desenhados)
    private void restaurarImagemOriginal() {
        if (originalBitmap != null) {
            // Restaurar a imagem original sem os pontos
            mutableBitmap = originalBitmap.copy(originalBitmap.getConfig(), true);
            imageView.setImageBitmap(mutableBitmap);
        }
    }

    // Método para verificar se um pixel é branco
    private boolean isWhite(int pixelColor) {
        // Verificar se o pixel é branco (cor RGB (255, 255, 255))
        return Color.red(pixelColor) == 255 && Color.green(pixelColor) == 255 && Color.blue(pixelColor) == 255;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);  // Remover callbacks quando a atividade for destruída
    }


    // Método para preencher os pontos da linha de chegada
    private void linhaChegada() {
        int chegadaX = 656;
        for (int i = 134; i <= 262; i++) {
            linhadeChegada.add(new int[]{chegadaX, i});
        }
    }
}
