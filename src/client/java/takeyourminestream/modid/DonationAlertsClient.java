package takeyourminestream.modid;

import takeyourminestream.modid.messages.MessageSpawner;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class DonationAlertsClient {
    private final MessageSpawner messageSpawner;
    private Thread listenThread;
    private volatile boolean running = false;

    public DonationAlertsClient(MessageSpawner messageSpawner) {
        this.messageSpawner = messageSpawner;
        connect();
    }

    private void connect() {
        running = true;
        listenThread = new Thread(this::listenLoop, "DonationAlerts-Listen");
        listenThread.setDaemon(true);
        listenThread.start();
    }

    private void listenLoop() {
        String urlStr = ModConfig.DONATIONALERTS_WIDGET_URL;
        if (urlStr == null || urlStr.isEmpty()) {
            urlStr = "https://www.donationalerts.com/widget/alerts?group_id=" + ModConfig.DONATIONALERTS_GROUP_ID + "&token=" + ModConfig.DONATIONALERTS_TOKEN;
        }
        if (urlStr == null || urlStr.isEmpty()) return;
        try {
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "text/event-stream");
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(0); // Бесконечно
            conn.connect();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                String line;
                StringBuilder eventData = new StringBuilder();
                while (running && (line = reader.readLine()) != null) {
                    if (line.startsWith("data:")) {
                        eventData.append(line.substring(5).trim());
                    } else if (line.isEmpty() && eventData.length() > 0) {
                        handleEvent(eventData.toString());
                        eventData.setLength(0);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("[DA] Ошибка подключения к Donation Alerts: " + e.getMessage());
        }
    }

    private void handleEvent(String data) {
        try {
            JsonObject obj = JsonParser.parseString(data).getAsJsonObject();
            String type = obj.has("type") ? obj.get("type").getAsString() : "";
            if (type.equals("donation") || type.equals("test_donation")) {
                JsonObject donation = obj.getAsJsonObject("message");
                if (donation == null) donation = obj;
                String username = donation.has("username") ? donation.get("username").getAsString() : "?";
                String message = donation.has("message") ? donation.get("message").getAsString() : "";
                String amount = donation.has("amount") ? donation.get("amount").getAsString() : "?";
                String currency = donation.has("currency") ? donation.get("currency").getAsString() : "";
                String text = "[DA] " + username + ": " + message + " (" + amount + " " + currency + ")";
                messageSpawner.setCurrentMessage(text);
            }
        } catch (Exception e) {
            System.err.println("[DA] Ошибка парсинга события: " + e.getMessage());
        }
    }

    public void disconnect() {
        running = false;
        if (listenThread != null) {
            try { listenThread.join(1000); } catch (InterruptedException ignored) {}
        }
    }
} 