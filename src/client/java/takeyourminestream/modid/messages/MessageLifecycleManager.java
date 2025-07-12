package takeyourminestream.modid.messages;

import net.minecraft.client.MinecraftClient;
import takeyourminestream.modid.ModConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * Управляет жизненным циклом сообщений
 */
public class MessageLifecycleManager {
    private final List<Message> activeMessages = new ArrayList<>();
    private int tickCounter = 0;
    
    /**
     * Обновляет состояние всех активных сообщений
     * @param client Minecraft клиент
     */
    public void updateMessages(MinecraftClient client) {
        if (client.player == null || client.world == null) return;
        
        tickCounter++;
        
        // Обновляем замороженное время для всех сообщений
        if (ModConfig.ENABLE_FREEZING_ON_VIEW) {
            for (Message message : activeMessages) {
                if (MessageViewDetector.isPlayerLookingAtMessage(client, message.getPosition())) {
                    message.incrementFrozenTicks();
                }
            }
        }
        
        // Удаляем старые сообщения с учетом замороженного времени
        int fallTicks = ModConfig.MESSAGE_FALL_TICKS;
        int removeAfter = ModConfig.MESSAGE_LIFETIME_TICKS + fallTicks;
        if (ModConfig.ENABLE_FREEZING_ON_VIEW) {
            activeMessages.removeIf(message -> {
                int effectiveAge = message.getEffectiveAge(tickCounter);
                if (effectiveAge > removeAfter) {
                    // TODO: Add particle effects when message expires (e.g., crying obsidian break particles)
                    return true;
                }
                return false;
            });
        } else {
            activeMessages.removeIf(message -> {
                int effectiveAge = tickCounter - (int)message.getSpawnTick();
                if (effectiveAge > removeAfter) {
                    // TODO: Add particle effects when message expires (e.g., crying obsidian break particles)
                    return true;
                }
                return false;
            });
        }
    }
    
    /**
     * Добавляет новое сообщение в список активных
     * @param message сообщение для добавления
     */
    public void addMessage(Message message) {
        activeMessages.add(message);
    }
    
    /**
     * Очищает все активные сообщения
     */
    public void clearAllMessages() {
        activeMessages.clear();
        tickCounter = 0;
    }
    
    /**
     * Возвращает список активных сообщений
     * @return список активных сообщений
     */
    public List<Message> getActiveMessages() {
        return activeMessages;
    }
    
    /**
     * Возвращает текущий счетчик тиков
     * @return текущий счетчик тиков
     */
    public int getTickCounter() {
        return tickCounter;
    }
} 