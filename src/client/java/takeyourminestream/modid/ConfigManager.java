package takeyourminestream.modid;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import takeyourminestream.modid.config.ModConfigData;
import takeyourminestream.modid.interfaces.IConfigManager;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class ConfigManager implements IConfigManager {
    private static final Logger LOGGER = Logger.getLogger(ConfigManager.class.getName());
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File CONFIG_FILE = new File(FabricLoader.getInstance().getConfigDir().toFile(), "take-your-minestream.json");
    
    private static ConfigManager instance;
    private ModConfigData configData;
    private final Map<String, Object> configCache = new HashMap<>();

    private ConfigManager() {
        this.configData = new ModConfigData();
        loadConfig();
    }

    public static ConfigManager getInstance() {
        if (instance == null) {
            instance = new ConfigManager();
        }
        return instance;
    }

    @Override
    public void loadConfig() {
        if (CONFIG_FILE.exists()) {
            try (FileReader reader = new FileReader(CONFIG_FILE)) {
                ModConfigData loadedData = GSON.fromJson(reader, ModConfigData.class);
                if (loadedData != null) {
                    this.configData = loadedData;
                    updateConfigCache();
                    LOGGER.info("Конфигурация успешно загружена");
                }
            } catch (IOException e) {
                LOGGER.severe("Ошибка при загрузке конфигурации: " + e.getMessage());
                sendPlayerMessage("§cОшибка при загрузке конфигурации");
            }
        } else {
            LOGGER.info("Файл конфигурации не найден, используются значения по умолчанию");
            saveConfig(); // Создаем файл с значениями по умолчанию
        }
    }

    @Override
    public void saveConfig() {
        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            GSON.toJson(configData, writer);
            updateConfigCache();
            LOGGER.info("Конфигурация успешно сохранена");
        } catch (IOException e) {
            LOGGER.severe("Ошибка при сохранении конфигурации: " + e.getMessage());
            sendPlayerMessage("§cОшибка при сохранении конфигурации");
        }
    }

    @Override
    public Object getConfigValue(String key) {
        return configCache.get(key);
    }

    @Override
    public void setConfigValue(String key, Object value) {
        switch (key) {
            case "twitchChannelName":
                configData.setTwitchChannelName((String) value);
                break;
            case "messageLifetimeTicks":
                configData.setMessageLifetimeTicks((Integer) value);
                break;
            case "messageFallTicks":
                configData.setMessageFallTicks((Integer) value);
                break;
            case "enableFreezingOnView":
                configData.setEnableFreezingOnView((Boolean) value);
                break;
            case "maxFreezeDistance":
                configData.setMaxFreezeDistance((Double) value);
                break;
            case "messagesInFrontOfPlayerOnly":
                configData.setMessagesInFrontOfPlayerOnly((Boolean) value);
                break;
            case "messageSpawnMode":
                configData.setMessageSpawnMode((takeyourminestream.modid.config.MessageSpawnMode) value);
                break;
            case "enableAutomoderation":
                configData.setEnableAutomoderation((Boolean) value);
                break;
            case "messageScale":
                configData.setMessageScale((takeyourminestream.modid.config.MessageScale) value);
                break;
            case "showMessageBackground":
                configData.setShowMessageBackground((Boolean) value);
                break;
            case "followPlayer":
                configData.setFollowPlayer((Boolean) value);
                break;
            default:
                LOGGER.warning("Неизвестный ключ конфигурации: " + key);
                return;
        }
        updateConfigCache();
        saveConfig();
    }

    private void updateConfigCache() {
        configCache.clear();
        configCache.put("twitchChannelName", configData.getTwitchChannelName());
        configCache.put("messageLifetimeTicks", configData.getMessageLifetimeTicks());
        configCache.put("messageFallTicks", configData.getMessageFallTicks());
        configCache.put("enableFreezingOnView", configData.isEnableFreezingOnView());
        configCache.put("maxFreezeDistance", configData.getMaxFreezeDistance());
        configCache.put("messagesInFrontOfPlayerOnly", configData.isMessagesInFrontOfPlayerOnly());
        configCache.put("messageSpawnMode", configData.getMessageSpawnMode());
        configCache.put("enableAutomoderation", configData.isEnableAutomoderation());
        configCache.put("particleMinCount", configData.getParticleMinCount());
        configCache.put("particleMaxCount", configData.getParticleMaxCount());
        configCache.put("particleLifetimeTicks", configData.getParticleLifetimeTicks());
        configCache.put("nickColors", configData.getNickColors());
        configCache.put("messageScale", configData.getMessageScale());
        configCache.put("showMessageBackground", configData.isShowMessageBackground());
        configCache.put("followPlayer", configData.isFollowPlayer());
    }

    public ModConfigData getConfigData() {
        return configData;
    }

    private void sendPlayerMessage(String message) {
        if (MinecraftClient.getInstance().player != null) {
            MinecraftClient.getInstance().player.sendMessage(Text.of(message), false);
        }
    }
} 