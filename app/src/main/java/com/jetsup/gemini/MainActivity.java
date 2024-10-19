package com.jetsup.gemini;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.android.material.textfield.TextInputEditText;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    // Access your API key as a Build Configuration variable
    final String GEMINI_KEY = BuildConfig.GEMINI_API_KEY;
    Button askButton;
    TextInputEditText promptInput;
    TextView responseText;
    private GenerativeModelFutures model;

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
        // views
        askButton = findViewById(R.id.ask_button);
        promptInput = findViewById(R.id.prompt_input);
        responseText = findViewById(R.id.response_text);

        askButton.setOnClickListener(v -> askGemini());

        // Specify a Gemini model appropriate for your use case
        /* modelName */
        // Access your API key as a Build Configuration variable (see "Set up your API key"
        // above)
        /* apiKey */
        GenerativeModel gm = new GenerativeModel(
                /* modelName */ "gemini-1.5-flash",
                // Access your API key as a Build Configuration variable (see "Set up your API key"
                // above)
                /* apiKey */ GEMINI_KEY);
        model = GenerativeModelFutures.from(gm);
    }

    void askGemini() {
        String prompt = Objects.requireNonNull(promptInput.getText()).toString();
        Content content = new Content.Builder().addText(prompt).build();
//        ListenableFuture<GenerateContentResponse> response = gm.generateContent(content);
        ListenableFuture<GenerateContentResponse> response = model.generateContent(content);
        Futures.addCallback(
                response,
                new FutureCallback<GenerateContentResponse>() {
                    @Override
                    public void onSuccess(GenerateContentResponse result) {
                        String resultText = result.getText();
                        responseText.setText(resultText);
                        // clear the prompt
                        promptInput.setText("");
                    }

                    @Override
                    public void onFailure(@NonNull Throwable t) {
                        t.printStackTrace();
                    }
                },
                Runnable::run);
    }
}