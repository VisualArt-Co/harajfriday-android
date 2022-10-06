package com.benAbdelWahed.models;

import java.util.ArrayList;

public class FirebaseBody {
    private ArrayList<String> registration_ids;
    private Data data;
    private boolean content_available = true;

    public FirebaseBody(ArrayList<String> deviceTokens, String message, String title, int userId) {
        this.registration_ids = deviceTokens;
        data = new Data(message, title, userId);
    }

    class Data {
        String message;
        String title;
        int userId;

        public Data(String message, String title, int userId) {
            this.message = message;
            this.title = title;
            this.userId = userId;
        }
    }
}
