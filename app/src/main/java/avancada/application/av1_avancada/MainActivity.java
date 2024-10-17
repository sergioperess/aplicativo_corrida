package avancada.application.av1_avancada;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
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

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import avancada.application.funclibrary.Test;

public class MainActivity extends AppCompatActivity {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private boolean isPaused = false;
    private boolean isStarted = false;
    private ImageView imageView;
    private Bitmap bitmap, mutableBitmap;
    private Canvas canvas;
    private Handler handler;
    private Runnable runnable;
    private List<Car> carList = new ArrayList<>(); // Lista de carros
    private EditText campoQuantidadeCarros; // Campo para inserir quantidade de carros
    private final List<int[]> linhadeChegada = new ArrayList<>();

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


        imageView = findViewById(R.id.myImageView);
        campoQuantidadeCarros = findViewById(R.id.campoQuantidadeCarros);
        Button botaoStart = findViewById(R.id.botaoStart);

        // Carregar a imagem da pasta drawable como Bitmap
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.pista);
        mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        canvas = new Canvas(mutableBitmap);

        // Preenche os pontos da linha de chegada
        linhaChegada();

        Test tes1 = new Test();
        tes1.test();

        // Configurar o comportamento do botão Start
        botaoStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!isStarted) {
                    // Verificar se o campo foi preenchido
                    String quantidadeTexto = campoQuantidadeCarros.getText().toString();

                    if (!quantidadeTexto.isEmpty()) {
                        isStarted = true;
                        int quantidadeCarros = Integer.parseInt(quantidadeTexto);
                        if (quantidadeCarros > 0) {
                            createCars(quantidadeCarros); // Criar os carros
                            startMovement(); // Iniciar a movimentação
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
        Button pauseButton = findViewById(R.id.botaoPause);

        // Definir a lógica do botão de pausa
        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isStarted) {
                    if (!isPaused) {
                        isPaused = true;
                        pauseButton.setText("Retomar");
                        Toast.makeText(MainActivity.this, "Pausado", Toast.LENGTH_SHORT).show();

                        // Salvar o estado de cada carro ao pausar
                        saveCarState();

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

                    saveCarState();

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

        Button restartButton = findViewById(R.id.botaoReiniciar); // Certifique-se que o botão está no layout

        restartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //loadCarStatesFromFirestore(); // Método para carregar os estados dos carros
            }
        });
    }


    // Método para iniciar a movimentação dos carros
    private void startMovement() {
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                if (!isPaused) {
                    moveCars(); // Mover os carros
                    handler.postDelayed(this, 1); // Executar a cada 500ms
                }
            }
        };
        handler.post(runnable); // Iniciar a movimentação
    }

    // Método para mover todos os carros
    private void moveCars() {
        // Limpa o Bitmap antes de desenhar os carros
        mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true); // Restaura a imagem da pista
        canvas = new Canvas(mutableBitmap); // Cria um novo canvas

        for (Car car : carList) {

            // Verifica se o carro passou pela linha de chegada
            if (verificarLinhaChegada(car)) {
                car.setLaps(car.getLaps() + 1); // Incrementa o número de voltas do carro
                Log.d("Voltas", car.getNome() + " completou " + car.getLaps() + " voltas.");

                // Exibe uma mensagem ao jogador
                Toast.makeText(MainActivity.this, "Carro de cor " + car.getNome() + " completou " + car.getLaps() + " voltas.", Toast.LENGTH_SHORT).show();
            }
            car.move(mutableBitmap, canvas); // Move cada carro
            Log.d(String.valueOf(car.getNome()), "X:" + car.getX() + ", Y:" + car.getY()
                    + ", Distancia: " + car.getDistance());
            Log.d(String.valueOf(car.getNome()), "Map:" + car.getSensorData());
            Log.d(String.valueOf(car.getNome()), "Panalidades:" + car.getPenalty());
        }

        // Redesenha a imagem na ImageView
        imageView.setImageBitmap(mutableBitmap);
    }

    // Método para criar os carros 262, 134 -> 394, 522
    private void createCars(int quantidadeCarros) {
        carList.clear(); // Limpa a lista de carros

        // Ângulo inicial em radianos (90 graus)
        double angleIncrement = Math.toRadians(5); // Converte 5 graus para radianos
        double radius = 458; // Raio do círculo
        double centerX = 656; // Coordenada X do centro
        double centerY = 656; // Coordenada Y do centro

        // Define o ângulo inicial como 90 graus (ou pi/2 radianos)
        double initialAngle = Math.toRadians(-90);

        for (int i = 0; i < quantidadeCarros; i++) {
            // Calcula o ângulo atual em radianos, subtraindo para movimento anti-horário
            double angle = initialAngle - (i * angleIncrement);

            // Calcula as coordenadas (x, y) do carro usando a fórmula de coordenadas polares
            int x = (int) (centerX + radius * Math.cos(angle));
            int y = (int) (centerY + radius * Math.sin(angle));

            int color = getRandomColor();
            Car car = new Car("Carro " + i,x, y, color,21);
            carList.add(car);
        }
    }

    // Método para preencher os pontos da linha de chegada
    private void linhaChegada() {
        int chegadaX = 656;
        for (int i = 134; i <= 262; i++) {
            linhadeChegada.add(new int[]{chegadaX, i});
        }
    }

    // Método para verificar se um carro passou pela linha de chegada
    private boolean verificarLinhaChegada(Car car) {
        for (int[] ponto : linhadeChegada) {
            if (ponto[0] == car.getX() && ponto[1] == car.getY()) {
                return true;
            }
        }
        return false;
    }

    private Map<String, Integer> convertSensorMap(Map<Integer, Integer> sensorMap) {
        Map<String, Integer> stringMap = new HashMap<>();
        for (Map.Entry<Integer, Integer> entry : sensorMap.entrySet()) {
            stringMap.put(String.valueOf(entry.getKey()), entry.getValue());
        }
        return stringMap;
    }


    // Método para salvar o estado atualizado dos carros
    private void saveCarState() {
        db.collection("carros")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<String> currentCarNames = new ArrayList<>();
                    for (Car car : carList) {
                        currentCarNames.add(car.getNome());
                    }

                    // Apagar os carros que não estão mais na lista
                    for (DocumentSnapshot snapshot : queryDocumentSnapshots.getDocuments()) {
                        String carName = snapshot.getId();
                        if (!currentCarNames.contains(carName)) {
                            db.collection("carros").document(carName).delete();
                        }
                    }

                    // Salvar o estado dos carros atuais
                    for (Car car : carList) {
                        Map<String, Object> carData = new HashMap<>();
                        carData.put("nome", car.getNome());
                        carData.put("x", car.getX());
                        carData.put("y", car.getY());
                        carData.put("color", car.getColor());
                        carData.put("d", car.getD());
                        carData.put("distance", car.getDistance());
                        carData.put("direction", car.getDirection());
                        carData.put("penalty", car.getPenalty());
                        carData.put("laps", car.getLaps());
                        carData.put("sensor", convertSensorMap(car.getSensor())); // O mapa de sensores

                        // Salva no Firestore usando o nome do carro como o ID do documento
                        db.collection("carros").document(car.getNome())
                                .set(carData)
                                .addOnSuccessListener(aVoid -> {
                                    Log.d("Firestore", "Estado do carro salvo com sucesso!");
                                })
                                .addOnFailureListener(e -> {
                                    Log.w("Firestore", "Erro ao salvar o estado do carro", e);
                                });
                    }
                })
                .addOnFailureListener(e -> Log.w("Firestore", "Erro ao acessar dados antigos", e));
    }


    /*private void loadCarStatesFromFirestore() {

        // Limpa a lista de carros antes de restaurar os estados
        carList.clear();

        // Aqui, assumimos que você tem uma coleção "cars" onde salvou o estado de cada carro
        db.collection("carros").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (!task.getResult().isEmpty()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // Obter os dados de cada carro
                                String nome = document.getString("nome");
                                int x = document.getLong("x").intValue();
                                int y = document.getLong("y").intValue();
                                int color = document.getLong("color").intValue();
                                int d = document.getLong("d").intValue();
                                int distance = document.getLong("distance").intValue();
                                int direction = document.getLong("direction").intValue();
                                int penalty = document.getLong("penalty").intValue();
                                int laps = document.getLong("laps").intValue();

                                // Converter o mapa de sensores salvo como String para Integer
                                Map<String, Long> sensorMap = (Map<String, Long>) document.get("sensor");
                                Map<Integer, Integer> sensor = new HashMap<>();
                                for (Map.Entry<String, Long> entry : sensorMap.entrySet()) {
                                    sensor.put(Integer.parseInt(entry.getKey()), entry.getValue().intValue());
                                }

                                // Criar o carro com os valores restaurados
                                Car car = new Car(nome, x, y, color, d);
                                car.setDistance(distance);
                                car.setDirection(direction);
                                car.setPenalty(penalty);
                                car.setLaps(laps);
                                car.setSensor(sensor);

                                // Adicionar o carro restaurado na lista de carros da corrida
                                addCarToRace(car);
                            }

                            // Reiniciar a corrida
                            restartRace();
                        }
                        else {
                            Toast.makeText(MainActivity.this, "Nenhum estado de corrida salvo encontrado.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.d("Firestore", "Erro ao carregar os estados: ", task.getException());
                    }
                });
    }

    private void addCarToRace(Car car) {
        // Aqui você implementa a lógica de adicionar o carro na corrida,
        // como por exemplo, adicionar ele no Canvas ou em uma lista de carros ativos.
        carList.add(car);  // Supondo que você tem uma lista chamada carsList
    }
    private void restartRace() {
        isPaused = false;
        isStarted = true;

        // Interrompe o handler atual para evitar execução duplicada
        if (handler != null && runnable != null) {
            handler.removeCallbacks(runnable);
        }

        // Redesenhar a imagem da pista antes de reiniciar a corrida
        mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        canvas = new Canvas(mutableBitmap);

        // Atualizar a visualização com os carros restaurados
        moveCars(); // Mover os carros para os seus estados restaurados

        handler = new Handler(); // Reiniciar o handler para controlar o movimento
        runnable = new Runnable() {
            @Override
            public void run() {
                if (!isPaused) {
                    moveCars(); // Mover os carros
                    handler.postDelayed(this, 1); // Executar a cada 1ms
                }
            }
        };
        handler.post(runnable);  // Iniciar a movimentação
    }*/


    // Método para gerar uma cor aleatória que não seja branca
    private int getRandomColor() {
        Random random = new Random();
        int color;
        do {
            color = Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256));
        } while (color == Color.WHITE); // Garantir que a cor não seja branca
        return color;
    }
}
