package avancada.application.funclibrary;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirestoreManager<T> {
    private FirebaseFirestore db;

    public FirestoreManager() {
        db = FirebaseFirestore.getInstance();
    }

    // Método para salvar um objeto no Firestore
    public void save(String collectionName, String documentId, Map<String, Object> data) {
        db.collection(collectionName).document(documentId)
                .set(data, SetOptions.merge()) // Usa merge para atualizar documentos existentes
                .addOnSuccessListener(aVoid -> {
                    System.out.println( "Estado salvo com sucesso para o documento: " + documentId);
                })
                .addOnFailureListener(e -> {
                    System.out.println("Erro ao salvar o estado no documento: " + documentId);
                });
    }

    // Método para buscar todos os documentos de uma coleção
    public void fetchCollection(String collectionName, FirestoreCallback<List<Map<String, Object>>> callback) {
        db.collection(collectionName).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<Map<String, Object>> items = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // Adicionar os dados do documento em um Map
                                items.add(document.getData()); // Retorna os dados do documento como um Map
                            }
                            callback.onSuccess(items);
                        } else {
                            callback.onFailure(task.getException());
                        }
                    }
                });
    }

    // Callback para manipulação de resultados de busca
    public interface FirestoreCallback<T> {
        void onSuccess(T items);
        void onFailure(Exception e);
    }
}
