package com.maplerad.twittersearch;

/**
 * Created by Polygod on 4/5/18.
 */


import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.translate.Translate;
import com.google.api.services.translate.model.TranslationsListResponse;
import com.google.api.services.translate.model.TranslationsResource;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.List;

public class GoogleTranslator {
    final static String APP_KEY = "AIzaSyD8oEvLIWzH8Nk_YRItW-YCzjPBcrXgnsw";

    public static String translateTo(String toLanguage, String sourcePhrase) throws Exception {
        try {
            Translate translate = buildTranslate();
            return translate(translate, toLanguage, sourcePhrase);
        } catch (Exception e) {
            e.printStackTrace();
            return "Error in performing translation";
        }
    }

    private static String translate(Translate translate, String toLanguage, String phrase) throws IOException {
        Translate.Translations.List list = buildTranslateList(translate, toLanguage, phrase);
        TranslationsListResponse response = list.execute();
        List<TranslationsResource> translationResources = response.getTranslations();
        try {
            return translationResources.get(0).getTranslatedText();
        } catch (Exception e) {
            return "Could not translate";
        }
    }

    private static Translate buildTranslate() throws GeneralSecurityException, IOException {
        HttpTransport transport = AndroidHttp.newCompatibleTransport();
        JacksonFactory jsonFactory = JacksonFactory.getDefaultInstance();

        return new Translate.Builder(transport, jsonFactory, null).setApplicationName("TwitterApp").build();
    }

    private static Translate.Translations.List buildTranslateList(Translate translate, String toLanguage, String phrase)
            throws IOException {
        Translate.Translations.List list = translate.new Translations().list(Arrays.asList(phrase),toLanguage);
        list.setKey(APP_KEY);
        return list;
    }
}