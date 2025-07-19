package takeyourminestream.modid;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import takeyourminestream.modid.interfaces.ITwitchManager;
import takeyourminestream.modid.interfaces.IConfigManager;
import takeyourminestream.modid.messages.MessageSpawner;
import java.util.logging.Logger;

public class TwitchManager implements ITwitchManager {
    private static final Logger LOGGER = Logger.getLogger(TwitchManager.class.getName());
    private static TwitchManager instance;
    
    private TwitchChatClient twitchChatClient;
    private String lastTwitchChannelName;
    private boolean twitchConnected = false;
    private MessageSpawner messageSpawner;
    private final IConfigManager configManager;

    private TwitchManager(IConfigManager configManager) {
        this.configManager = configManager;
        this.lastTwitchChannelName = (String) configManager.getConfigValue("twitchChannelName");
    }

    public static TwitchManager getInstance(IConfigManager configManager) {
        if (instance == null) {
            instance = new TwitchManager(configManager);
        }
        return instance;
    }

    @Override
    public void connect(MessageSpawner spawner) {
        this.messageSpawner = spawner;
        String channelName = (String) configManager.getConfigValue("twitchChannelName");
        
        if (twitchChatClient == null) {
            try {
                sendPlayerMessage("§aПодключение к Twitch-чату '" + channelName + "'...");
                twitchChatClient = new TwitchChatClient(channelName, messageSpawner);
                twitchConnected = true;
                lastTwitchChannelName = channelName;
                sendPlayerMessage("§aУспешно подключено к Twitch-чату!");
                LOGGER.info("Подключено к Twitch-каналу: " + channelName);
            } catch (Exception e) {
                LOGGER.severe("Ошибка при подключении к Twitch: " + e.getMessage());
                sendPlayerMessage("§cОшибка при подключении к Twitch: " + e.getMessage());
            }
        } else {
            sendPlayerMessage("§eУже подключен к Twitch-чату.");
        }
    }

    @Override
    public void disconnect() {
        if (twitchChatClient != null) {
            try {
                twitchChatClient.disconnect();
                twitchChatClient = null;
                twitchConnected = false;
                sendPlayerMessage("§aОтключено от Twitch-чата.");
                LOGGER.info("Отключено от Twitch-канала");
            } catch (Exception e) {
                LOGGER.severe("Ошибка при отключении от Twitch: " + e.getMessage());
                sendPlayerMessage("§cОшибка при отключении от Twitch: " + e.getMessage());
            }
        } else {
            sendPlayerMessage("§eНе подключен к Twitch-чату.");
        }
    }

    @Override
    public void onChannelNameChanged(String newChannelName) {
        if (twitchConnected && !newChannelName.equals(lastTwitchChannelName)) {
            try {
                // Переподключаемся к новому каналу
                if (twitchChatClient != null) {
                    twitchChatClient.disconnect();
                }
                twitchChatClient = new TwitchChatClient(newChannelName, messageSpawner);
                lastTwitchChannelName = newChannelName;
                sendPlayerMessage("§aПереподключено к Twitch-каналу: " + newChannelName);
                LOGGER.info("Переподключено к Twitch-каналу: " + newChannelName);
            } catch (Exception e) {
                LOGGER.severe("Ошибка при переподключении к Twitch: " + e.getMessage());
                sendPlayerMessage("§cОшибка при переподключении к Twitch: " + e.getMessage());
            }
        }
        lastTwitchChannelName = newChannelName;
    }

    @Override
    public boolean isConnected() {
        return twitchConnected;
    }

    private void sendPlayerMessage(String message) {
        if (MinecraftClient.getInstance().player != null) {
            MinecraftClient.getInstance().player.sendMessage(Text.of(message), false);
        }
    }
} 