package takeyourminestream.modid;

import takeyourminestream.modid.config.ModConfigData;

/**
 * Утилитарный класс для доступа к конфигурации
 * @deprecated Используйте ConfigManager.getInstance().getConfigData() вместо этого класса
 */
@Deprecated
public class ModConfig {
    /**
     * Получает текущую конфигурацию
     * @return объект конфигурации
     */
    public static ModConfigData getCurrentConfig() {
        return ConfigManager.getInstance().getConfigData();
    }

    // Обратная совместимость - статические поля теперь возвращают значения из ConfigManager
    public static String getTWITCH_CHANNEL_NAME() {
        return (String) ConfigManager.getInstance().getConfigValue("twitchChannelName");
    }

    public static int getMESSAGE_LIFETIME_TICKS() {
        return (Integer) ConfigManager.getInstance().getConfigValue("messageLifetimeTicks");
    }

    public static int getMESSAGE_FALL_TICKS() {
        return (Integer) ConfigManager.getInstance().getConfigValue("messageFallTicks");
    }

    public static String[] getNICK_COLORS() {
        return (String[]) ConfigManager.getInstance().getConfigValue("nickColors");
    }

    public static boolean isENABLE_FREEZING_ON_VIEW() {
        return (Boolean) ConfigManager.getInstance().getConfigValue("enableFreezingOnView");
    }

    public static double getMAX_FREEZE_DISTANCE() {
        return (Double) ConfigManager.getInstance().getConfigValue("maxFreezeDistance");
    }

    public static boolean isMESSAGES_IN_FRONT_OF_PLAYER_ONLY() {
        return (Boolean) ConfigManager.getInstance().getConfigValue("messagesInFrontOfPlayerOnly");
    }

    public static int getPARTICLE_MIN_COUNT() {
        return (Integer) ConfigManager.getInstance().getConfigValue("particleMinCount");
    }

    public static int getPARTICLE_MAX_COUNT() {
        return (Integer) ConfigManager.getInstance().getConfigValue("particleMaxCount");
    }

    public static int getPARTICLE_LIFETIME_TICKS() {
        return (Integer) ConfigManager.getInstance().getConfigValue("particleLifetimeTicks");
    }

    public static boolean isENABLE_AUTOMODERATION() {
        return (Boolean) ConfigManager.getInstance().getConfigValue("enableAutomoderation");
    }

    public static void setENABLE_FREEZING_ON_VIEW(boolean value) {
        ConfigManager.getInstance().setConfigValue("enableFreezingOnView", value);
    }

    public static void setMESSAGES_IN_FRONT_OF_PLAYER_ONLY(boolean value) {
        ConfigManager.getInstance().setConfigValue("messagesInFrontOfPlayerOnly", value);
    }

    public static void setENABLE_AUTOMODERATION(boolean value) {
        ConfigManager.getInstance().setConfigValue("enableAutomoderation", value);
    }
    
    public static takeyourminestream.modid.config.MessageSpawnMode getMESSAGE_SPAWN_MODE() {
        return (takeyourminestream.modid.config.MessageSpawnMode) ConfigManager.getInstance().getConfigValue("messageSpawnMode");
    }
    
    public static void setMESSAGE_SPAWN_MODE(takeyourminestream.modid.config.MessageSpawnMode value) {
        ConfigManager.getInstance().setConfigValue("messageSpawnMode", value);
    }
    
    public static takeyourminestream.modid.config.MessageScale getMESSAGE_SCALE() {
        return (takeyourminestream.modid.config.MessageScale) ConfigManager.getInstance().getConfigValue("messageScale");
    }
    
    public static void setMESSAGE_SCALE(takeyourminestream.modid.config.MessageScale value) {
        ConfigManager.getInstance().setConfigValue("messageScale", value);
    }

    public static boolean isSHOW_MESSAGE_BACKGROUND() {
        return (Boolean) ConfigManager.getInstance().getConfigValue("showMessageBackground");
    }

    public static void setSHOW_MESSAGE_BACKGROUND(boolean value) {
        ConfigManager.getInstance().setConfigValue("showMessageBackground", value);
    }
} 