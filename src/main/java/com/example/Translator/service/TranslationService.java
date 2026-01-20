package com.example.Translator.service;
import com.example.Translator.dto.TranslationRequest;
import com.example.Translator.dto.TranslationResponse;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

@Service
public class TranslationService {

    private Translate translate;

    @PostConstruct
    public void init() {
        try {
            String credentialsJson = System.getenv("GOOGLE_CREDENTIALS_JSON");

            if (credentialsJson == null || credentialsJson.isEmpty()) {
                System.err.println("GOOGLE_CREDENTIALS_JSON not set");
                return; // ❗ do NOT crash app
            }

            GoogleCredentials credentials = GoogleCredentials.fromStream(
                    new ByteArrayInputStream(credentialsJson.getBytes(StandardCharsets.UTF_8))
            );

            translate = TranslateOptions.newBuilder()
                    .setCredentials(credentials)
                    .build()
                    .getService();

            System.out.println("Google Translate initialized successfully");

        } catch (Exception e) {
            e.printStackTrace(); // log but don't crash
        }
    }

    public TranslationResponse translateText(TranslationRequest request) {
        if (translate == null) {
            throw new RuntimeException("Translation service not initialized");
        }

        Translation translation;

        if ("auto".equals(request.getSourceLang())) {
            translation = translate.translate(
                    request.getText(),
                    Translate.TranslateOption.targetLanguage(request.getTargetLang())
            );
        } else {
            translation = translate.translate(
                    request.getText(),
                    Translate.TranslateOption.sourceLanguage(request.getSourceLang()),
                    Translate.TranslateOption.targetLanguage(request.getTargetLang())
            );
        }

        return new TranslationResponse(
                translation.getTranslatedText(),
                request.getSourceLang(),
                request.getTargetLang()
        );
    }
}


//package com.example.Translator.service;
//
//import com.example.Translator.dto.TranslationRequest;
//import com.example.Translator.dto.TranslationResponse;
//import com.google.cloud.translate.Translate;
//import com.google.cloud.translate.TranslateOptions;
//import com.google.cloud.translate.Translation;
//import org.springframework.stereotype.Service;
//
//
//@Service
//public class TranslationService {
//
//    private final Translate translate =
//            TranslateOptions.getDefaultInstance().getService();
//
//    public TranslationResponse translateText(TranslationRequest request) {
//
//        Translation translation;
//
//        if ("auto".equals(request.getSourceLang())) {
//            translation = translate.translate(
//                    request.getText(),
//                    Translate.TranslateOption.targetLanguage(request.getTargetLang())
//            );
//        } else {
//            translation = translate.translate(
//                    request.getText(),
//                    Translate.TranslateOption.sourceLanguage(request.getSourceLang()),
//                    Translate.TranslateOption.targetLanguage(request.getTargetLang())
//            );
//        }
//
//        return new TranslationResponse(
//                translation.getTranslatedText(),
//                request.getSourceLang(),
//                request.getTargetLang()
//        );
//    }
//}
//
