package com.example.reto10;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    EditText inputDepartamento;
    Spinner spinnerEstado;
    Button btnBuscar;
    RecyclerView recyclerView;
    ProgressBar progressBar;
    TextView tvEmpty;

    List<CasoCovid> lista;
    CasoAdapter adapter;

    private static final String BASE_URL = "https://www.datos.gov.co/resource/gt2j-8ykr.json";

    String estadoSeleccionado = "Todos";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inputDepartamento = findViewById(R.id.inputDepartamento);
        spinnerEstado = findViewById(R.id.spinnerEstado);
        btnBuscar = findViewById(R.id.btnBuscar);
        recyclerView = findViewById(R.id.recyclerPuntos);
        progressBar = findViewById(R.id.progressBar);
        tvEmpty = findViewById(R.id.tvEmpty);

        // Configurar RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        lista = new ArrayList<>();
        adapter = new CasoAdapter(lista);
        recyclerView.setAdapter(adapter);

        // Configurar Spinner de estados
        String[] estados = {"Todos", "Leve", "Recuperado", "Fallecido"};
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, estados);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEstado.setAdapter(spinnerAdapter);

        spinnerEstado.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                estadoSeleccionado = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                estadoSeleccionado = "Todos";
            }
        });

        // Cargar datos iniciales
        obtenerDatos("", "Todos");

        btnBuscar.setOnClickListener(v -> {
            String dep = inputDepartamento.getText().toString().trim();
            obtenerDatos(dep, estadoSeleccionado);
        });
    }

    private void obtenerDatos(String departamento, String estado) {
        lista.clear();
        adapter.notifyDataSetChanged();
        tvEmpty.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

        String url = BASE_URL + "?$limit=100";
        String where = "";

        // Construir filtros dinámicos
        if (!departamento.isEmpty()) {
            where += "upper(departamento_nom)='" + departamento.toUpperCase().replace(" ", "%20") + "'";
        }

        if (!estado.equals("Todos")) {
            if (!where.isEmpty()) where += " AND ";
            where += "upper(estado)='" + estado.toUpperCase() + "'";
        }

        if (!where.isEmpty()) {
            url += "&$where=" + where;
        }

        RequestQueue queue = Volley.newRequestQueue(this);
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    procesarRespuesta(response);
                    progressBar.setVisibility(View.GONE);
                },
                error -> {
                    error.printStackTrace();
                    progressBar.setVisibility(View.GONE);
                    if (lista.isEmpty()) tvEmpty.setVisibility(View.VISIBLE);
                }
        );
        queue.add(request);
    }

    private void procesarRespuesta(JSONArray response) {
        try {
            for (int i = 0; i < response.length(); i++) {
                JSONObject obj = response.getJSONObject(i);

                String depNom = obj.optString("departamento_nom", "N/A");
                String munNom = obj.optString("ciudad_municipio_nom", "N/A");
                String sexo = obj.optString("sexo", "N/A");
                String edad = obj.optString("edad", "N/A");
                String estado = obj.optString("estado", "N/A");
                String fuente = obj.optString("fuente_tipo_contagio", "N/A");

                lista.add(new CasoCovid(depNom, munNom, sexo, edad, estado, fuente));
            }

            adapter.notifyDataSetChanged();
            tvEmpty.setVisibility(lista.isEmpty() ? View.VISIBLE : View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
            tvEmpty.setVisibility(View.VISIBLE);
        }
    }
}
