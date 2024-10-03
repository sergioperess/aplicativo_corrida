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
    private Button finishButton;
    private ImageView imageView;
    private Bitmap bitmap;
    private Bitmap mutableBitmap;
    private Canvas canvas;
    private Paint paint;
    private Random random;
    private EditText campoQuantidadeCarros;
    private Button botaoStart;
    // Lista para armazenar os pixels da linha de chegada (coordenadas x, y)
    private List<int[]> linhadeChegada = new ArrayList<>();

    private List<Car> carList = new ArrayList<>();


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
        botaoStart = findViewById(R.id.botaoStart);

        // Carregar a imagem e modificar o pixel
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.pista);
        mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        // Criar um Canvas associado ao Bitmap mutável
        canvas = new Canvas(mutableBitmap);

        int width = mutableBitmap.getWidth();
        int height = mutableBitmap.getHeight();
        Log.d("BitmapSize", "Largura: " + width + " Altura: " + height);

        // Criar um objeto Paint para definir a cor e o estilo do desenho
        paint = new Paint();
        //paint.setColor(Color.RED); // Definir a cor para vermelho

        // Desenhar um pixel na coordenada (250, 75)
        //canvas.drawCircle(656, 134, 10, paint);

        imageView = findViewById(R.id.myImageView);


        // Configurar o comportamento do botão Start
        botaoStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
                // Verificar se o campo foi preenchido
                String quantidadeTexto = campoQuantidadeCarros.getText().toString();

                if (!quantidadeTexto.isEmpty()) {

                    isStarted = true;

                    int quantidadeCarros = Integer.parseInt(quantidadeTexto);

                    if (quantidadeCarros > 0) {

                        // Procurar uma posição aleatória onde o pixel da imagem seja branco
                        int[] whitePixel = findRandomWhitePixel(mutableBitmap);
                        if (whitePixel != null) {
                            // Desenhar uma bolinha (ou pixel) nessa posição
                            drawRandomCircle(whitePixel[0], whitePixel[1]);
                            // Atualizar a ImageView com o novo Bitmap
                            imageView.setImageBitmap(mutableBitmap);
                        }

                        // Inicializando o Handler e o Runnable
                        handler = new Handler();
                        runnable = new Runnable() {
                            @Override
                            public void run() {
                                if (!isPaused) {
                                    // Sua tarefa em execução (simulada por um Toast aqui)
                                    Toast.makeText(MainActivity.this, "Tarefa em execução", Toast.LENGTH_SHORT).show();

                                    // Repetir a tarefa a cada 2 segundos
                                    handler.postDelayed(this, 2000);
                                }
                            }
                        };

                        // Iniciar a tarefa
                        handler.post(runnable);

                        // Adiciona os carros criados
                        for (int i = 0; i <= quantidadeCarros; i++) {
                            Car car = new Car("Carro " + i);
                            carList.add(car);
                        }

                        // Iniciar a próxima ação (por exemplo, iniciar uma corrida)
                        Toast.makeText(MainActivity.this, "Corrida iniciada com " + quantidadeCarros + " carros", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "Por favor, insira um número válido de carros", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Mostrar mensagem de erro se o campo estiver vazio
                    Toast.makeText(MainActivity.this, "Por favor, insira a quantidade de carros", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Inicializando o botão de pausa
        pauseButton = findViewById(R.id.botaoPause);

        // Definir a lógica do botão de pausa
        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isStarted){
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
                }else {
                    Toast.makeText(MainActivity.this, "Iniciar atividade", Toast.LENGTH_SHORT).show();
                }
            }
        });


        // Referenciar o botão de finalizar
        finishButton = findViewById(R.id.botaoFinish);

        // Definir a ação do botão de finalizar
        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finalizar a atividade
                if (!isPaused) {
                    isStarted = false;
                    // Parar completamente a tarefa
                    if (handler != null && runnable != null) {
                        handler.removeCallbacks(runnable); // Remove a tarefa
                        Toast.makeText(MainActivity.this, "Tarefa finalizada", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Retomar atividade", Toast.LENGTH_SHORT).show();
                }

                imageView.setImageResource(R.drawable.pista);
            }
        });
    }

    // Método para encontrar um pixel branco aleatório
    private int[] findRandomWhitePixel(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        for (int y = 0; y < width; y++) {
            for (int x = 0; x < height; x++) {
                // Verificar se o pixel nessa posição é branco
                int pixelColor = bitmap.getPixel(x, y);
                if (pixelColor == Color.WHITE) {
                    return new int[]{x, y}; // Retornar as coordenadas do pixel branco
                }
            }
        }

        // Retornar null se não encontrar nenhum pixel branco após várias tentativas
        return null;
    }

    // Método para desenhar uma bolinha colorida na posição (x, y)
    private void drawRandomCircle(int x, int y) {
        paint.setColor(getRandomColor()); // Definir cor aleatória para a bolinha
        canvas.drawCircle(x, y, 10, paint); // Desenhar uma bolinha com raio de 10 pixels
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
