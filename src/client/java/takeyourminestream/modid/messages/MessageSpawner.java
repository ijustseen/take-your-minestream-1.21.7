package takeyourminestream.modid.messages;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.MathHelper;

/**
 * Отвечает за спавн новых сообщений из очереди
 */
public class MessageSpawner {
    private final MessageQueue messageQueue;
    private final MessageLifecycleManager lifecycleManager;
    
    public MessageSpawner(MessageQueue messageQueue, MessageLifecycleManager lifecycleManager) {
        this.messageQueue = messageQueue;
        this.lifecycleManager = lifecycleManager;
        
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player != null && client.world != null) {
                // Обновляем жизненный цикл существующих сообщений
                lifecycleManager.updateMessages(client);
                
                // Проверяем, можно ли спавнить новое сообщение
                if (messageQueue.canSpawnMessage(lifecycleManager.getTickCounter())) {
                    var queued = messageQueue.dequeueMessage(lifecycleManager.getTickCounter());
                    if (queued != null) {
                        String messageText = queued.text;
                        Integer authorColor = queued.authorColorRgb;
                        var spawnMode = takeyourminestream.modid.ModConfig.getMESSAGE_SPAWN_MODE();
                        
                        // В HUD режиме создаем сообщение без 3D позиции
                        if (spawnMode == takeyourminestream.modid.config.MessageSpawnMode.HUD_WIDGET) {
                            // Для HUD режима создаем сообщение с нулевой позицией
                            var position = net.minecraft.util.math.Vec3d.ZERO;
                            var message = new Message(messageText, position, lifecycleManager.getTickCounter(), 0, 0, authorColor);
                            lifecycleManager.addMessage(message);
                        } else {
                            // Для 3D режимов генерируем позицию в пространстве
                            var position = (spawnMode == takeyourminestream.modid.config.MessageSpawnMode.FRONT_OF_PLAYER)
                                ? MessagePosition.generatePositionInFrontOfPlayer(client)
                                : MessagePosition.generateRandomPosition(client);
                            // Вычисляем yaw/pitch на игрока
                            var playerEyePos = client.player.getEyePos();
                            double dx = playerEyePos.x - position.x;
                            double dy = playerEyePos.y - position.y;
                            double dz = playerEyePos.z - position.z;
                            double distXZ = Math.sqrt(dx * dx + dz * dz);
                            float yaw = (float)(MathHelper.atan2(dz, dx) * (180.0 / Math.PI)) - 90.0f;
                            float pitch = (float)-(MathHelper.atan2(dy, distXZ) * (180.0 / Math.PI));
                            // Фиксированное мировое смещение от глаз игрока (НЕ зависит от взгляда)
                            var worldOffset = position.subtract(playerEyePos);
                            var message = new Message(messageText, position, lifecycleManager.getTickCounter(), yaw, pitch, authorColor, worldOffset);
                            lifecycleManager.addMessage(message);
                        }
                    }
                }
            }
        });
    }
    
    /**
     * Устанавливает новое сообщение для отображения
     * @param message текст сообщения (пустая строка для остановки)
     */
    public void setCurrentMessage(String message) {
        if (message.isEmpty()) {
            // Очищаем очередь и все активные сообщения
            messageQueue.clear();
            lifecycleManager.clearAllMessages();
        } else {
            // Добавляем сообщение в очередь
            messageQueue.enqueueMessage(message);
        }
    }

    public void setCurrentMessage(String message, Integer authorColorRgb) {
        if (message.isEmpty()) {
            messageQueue.clear();
            lifecycleManager.clearAllMessages();
        } else {
            messageQueue.enqueueMessage(message, authorColorRgb);
        }
    }
    
    /**
     * Возвращает менеджер жизненного цикла для доступа к активным сообщениям
     * @return менеджер жизненного цикла
     */
    public MessageLifecycleManager getLifecycleManager() {
        return lifecycleManager;
    }
} 