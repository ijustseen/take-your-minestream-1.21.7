package takeyourminestream.modid.config;

/**
 * Модель данных конфигурации мода
 */
public class ModConfigData {
    private String twitchChannelName = "ijustseen_you";
    private int messageLifetimeTicks = 80;
    private int messageFallTicks = 20;
    private boolean enableFreezingOnView = true;
    private double maxFreezeDistance = 15.0;
    private MessageSpawnMode messageSpawnMode = MessageSpawnMode.AROUND_PLAYER;
    private boolean enableAutomoderation = false;
    private int particleMinCount = 10;
    private int particleMaxCount = 20;
    private int particleLifetimeTicks = 20;
    private String[] nickColors = {"§c", "§9", "§a", "§5"};

    // Геттеры
    public String getTwitchChannelName() { return twitchChannelName; }
    public int getMessageLifetimeTicks() { return messageLifetimeTicks; }
    public int getMessageFallTicks() { return messageFallTicks; }
    public boolean isEnableFreezingOnView() { return enableFreezingOnView; }
    public double getMaxFreezeDistance() { return maxFreezeDistance; }
    public MessageSpawnMode getMessageSpawnMode() { return messageSpawnMode; }
    public boolean isEnableAutomoderation() { return enableAutomoderation; }
    public int getParticleMinCount() { return particleMinCount; }
    public int getParticleMaxCount() { return particleMaxCount; }
    public int getParticleLifetimeTicks() { return particleLifetimeTicks; }
    public String[] getNickColors() { return nickColors; }

    // Сеттеры
    public void setTwitchChannelName(String twitchChannelName) { this.twitchChannelName = twitchChannelName; }
    public void setMessageLifetimeTicks(int messageLifetimeTicks) { this.messageLifetimeTicks = messageLifetimeTicks; }
    public void setMessageFallTicks(int messageFallTicks) { this.messageFallTicks = messageFallTicks; }
    public void setEnableFreezingOnView(boolean enableFreezingOnView) { this.enableFreezingOnView = enableFreezingOnView; }
    public void setMaxFreezeDistance(double maxFreezeDistance) { this.maxFreezeDistance = maxFreezeDistance; }
    public void setMessageSpawnMode(MessageSpawnMode messageSpawnMode) { this.messageSpawnMode = messageSpawnMode; }
    public void setEnableAutomoderation(boolean enableAutomoderation) { this.enableAutomoderation = enableAutomoderation; }
    public void setParticleMinCount(int particleMinCount) { this.particleMinCount = particleMinCount; }
    public void setParticleMaxCount(int particleMaxCount) { this.particleMaxCount = particleMaxCount; }
    public void setParticleLifetimeTicks(int particleLifetimeTicks) { this.particleLifetimeTicks = particleLifetimeTicks; }
    public void setNickColors(String[] nickColors) { this.nickColors = nickColors; }
    
    // Методы для обратной совместимости
    public boolean isMessagesInFrontOfPlayerOnly() { 
        return messageSpawnMode == MessageSpawnMode.FRONT_OF_PLAYER; 
    }
    
    public void setMessagesInFrontOfPlayerOnly(boolean messagesInFrontOfPlayerOnly) { 
        this.messageSpawnMode = messagesInFrontOfPlayerOnly ? 
            MessageSpawnMode.FRONT_OF_PLAYER : MessageSpawnMode.AROUND_PLAYER; 
    }
} 