package avancada.application.av1_avancada;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private boolean isPaused = false;
    private Handler handler;
    private Runnable runnable;
    private Button pauseButton;

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


        // Configurar o comportamento do botão Start
        botaoStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Verificar se o campo foi preenchido
                String quantidadeTexto = campoQuantidadeCarros.getText().toString();

                if (!quantidadeTexto.isEmpty()) {
                    int quantidadeCarros = Integer.parseInt(quantidadeTexto);

                    if (quantidadeCarros > 0) {

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
                        for (int i = 0; i <= quantidadeCarros; i++){
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
            }
        });


        // Referenciar o botão de finalizar
        Button finishButton = findViewById(R.id.botaoFinish);

        // Definir a ação do botão de finalizar
        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finalizar a atividade
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);  // Remover callbacks quando a atividade for destruída
    }


    // Método para preencher os pontos da linha de chegada
    private void linhaChegada(){
        int chegadaX = 250;
        for (int i = 51; i <= 100; i++) {
            linhadeChegada.add(new int[]{chegadaX, i});
        }
    }

}
