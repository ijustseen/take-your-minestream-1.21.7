package takeyourminestream.modid;

import takeyourminestream.modid.messages.MessageSpawner;
import java.io.*;
import java.net.Socket;
import java.util.Random;
import takeyourminestream.modid.ModConfig;

public class TwitchChatClient {
    private Socket socket;
    private BufferedWriter writer;
    private BufferedReader reader;
    private Thread listenThread;
    private volatile boolean running = false;
    private final Random random = new Random();
    private final MessageSpawner messageSpawner;
    private String channelName;

    public TwitchChatClient(String channelName, MessageSpawner messageSpawner) {
        this.channelName = channelName;
        this.messageSpawner = messageSpawner;
        connect();
    }

    private String getRandomColor() {
        return ModConfig.getNICK_COLORS()[random.nextInt(ModConfig.getNICK_COLORS().length)];
    }

    private void connect() {
        try {
            socket = new Socket("irc.chat.twitch.tv", 6667);
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Анонимная авторизация (можно заменить на свой логин/токен при необходимости)
            String nick = "justinfan" + (10000 + random.nextInt(90000));
            sendRaw("PASS oauth:anonymous");
            sendRaw("NICK " + nick);
            sendRaw("JOIN #" + channelName.toLowerCase());

            running = true;
            listenThread = new Thread(this::listenLoop, "TwitchIRC-Listen");
            listenThread.setDaemon(true);
            listenThread.start();
            System.out.println("[IRC] Connected to Twitch IRC as " + nick + ", joined #" + channelName);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("[IRC] Failed to connect to Twitch IRC");
        }
    }

    private void sendRaw(String line) throws IOException {
        writer.write(line + "\r\n");
        writer.flush();
    }

    private void listenLoop() {
        try {
            String line;
            while (running && (line = reader.readLine()) != null) {
                if (line.startsWith("PING ")) {
                    sendRaw("PONG " + line.substring(5));
                } else if (line.contains("PRIVMSG")) {
                    // Пример строки:
                    // :username!username@username.tmi.twitch.tv PRIVMSG #channel :message text
                    int excl = line.indexOf('!');
                    int colon = line.indexOf(" :", 1);
                    int hash = line.indexOf("#");
                    if (excl > 1 && colon > 1 && hash > 1) {
                        String user = line.substring(1, excl);
                        String message = line.substring(colon + 2);
                        String channel = line.substring(hash + 1, line.indexOf(' ', hash));
                        System.out.println("[" + channel + "] " + user + ": " + message);
                        String filteredMessage = message;
                        if (ModConfig.isENABLE_AUTOMODERATION() && BanwordManager.getInstance().containsBanwords(message)) {
                            filteredMessage = BanwordManager.getInstance().filterBanwords(message);
                        }
                        messageSpawner.setCurrentMessage(getRandomColor() + user + ":§r " + filteredMessage);
                    }
                }
            }
        } catch (IOException e) {
            if (running) {
                e.printStackTrace();
            }
        }
        System.out.println("[IRC] Disconnected from Twitch IRC");
    }

    public void disconnect() {
        running = false;
        try {
            if (writer != null) sendRaw("PART #" + channelName.toLowerCase());
        } catch (IOException ignored) {}
        try {
            if (socket != null) socket.close();
        } catch (IOException ignored) {}
        if (listenThread != null) {
            try { listenThread.join(1000); } catch (InterruptedException ignored) {}
        }
        System.out.println("[IRC] Disconnected from Twitch chat.");
    }
} 