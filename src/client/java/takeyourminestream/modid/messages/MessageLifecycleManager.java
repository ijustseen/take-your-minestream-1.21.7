package takeyourminestream.modid.messages;

import net.minecraft.client.MinecraftClient;
import takeyourminestream.modid.ModConfig;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.util.math.Vec3d;
import java.util.HashSet;
import java.util.Set;

/**
 * Управляет жизненным циклом сообщений
 */
public class MessageLifecycleManager {
    private final List<Message> activeMessages = new ArrayList<>();
    private int tickCounter = 0;
    private final MessageParticleManager particleManager;
    private final Set<Message> spawnedParticlesForMessages = new HashSet<>();
    
    public MessageLifecycleManager(MessageParticleManager particleManager) {
        this.particleManager = particleManager;
    }
    
    /**
     * Обновляет состояние всех активных сообщений
     * @param client Minecraft клиент
     */
    public void updateMessages(MinecraftClient client) {
        if (particleManager != null) {
            particleManager.tick();
        }
        if (client.player == null || client.world == null) return;
        
        tickCounter++;
        
        // Обновляем замороженное время для всех сообщений
        if (ModConfig.isENABLE_FREEZING_ON_VIEW()) {
            for (Message message : activeMessages) {
                if (MessageViewDetector.isPlayerLookingAtMessage(client, message.getPosition(), message.getYaw(), message.getPitch(), message.getText(), 
                                                               tickCounter, message.getSpawnTick(), message.getFrozenTicks())) {
                    message.incrementFrozenTicks();
                }
            }
        }
        
        // Удаляем старые сообщения с учетом замороженного времени
        int fallTicks = ModConfig.getMESSAGE_FALL_TICKS();
        int removeAfter = ModConfig.getMESSAGE_LIFETIME_TICKS() + fallTicks;
        if (ModConfig.isENABLE_FREEZING_ON_VIEW()) {
            activeMessages.removeIf(message -> {
                int effectiveAge = message.getEffectiveAge(tickCounter);
                // Спавним партиклы за тик до удаления
                if (effectiveAge == removeAfter - 1 && !spawnedParticlesForMessages.contains(message)) {
                    if (particleManager != null) {
                        Vec3d finalPos = MessageViewDetector.calculateFallingPosition(
                            message.getPosition(),
                            effectiveAge,
                            message.getYaw(),
                            message.getPitch()
                        );
                        MessageParticleSpawner.spawnParticlesForMessage(message, particleManager, client, finalPos);
                        spawnedParticlesForMessages.add(message);
                    }
                }
                if (effectiveAge > removeAfter) {
                    spawnedParticlesForMessages.remove(message);
                    return true;
                }
                return false;
            });
        } else {
            activeMessages.removeIf(message -> {
                int effectiveAge = tickCounter - (int)message.getSpawnTick();
                // Спавним партиклы за тик до удаления
                if (effectiveAge == removeAfter - 1 && !spawnedParticlesForMessages.contains(message)) {
                    if (particleManager != null) {
                        Vec3d finalPos = MessageViewDetector.calculateFallingPosition(
                            message.getPosition(),
                            effectiveAge,
                            message.getYaw(),
                            message.getPitch()
                        );
                        MessageParticleSpawner.spawnParticlesForMessage(message, particleManager, client, finalPos);
                        spawnedParticlesForMessages.add(message);
                    }
                }
                if (effectiveAge > removeAfter) {
                    spawnedParticlesForMessages.remove(message);
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