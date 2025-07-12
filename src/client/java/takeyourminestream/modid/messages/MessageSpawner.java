package takeyourminestream.modid.messages;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;

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
                    String messageText = messageQueue.dequeueMessage(lifecycleManager.getTickCounter());
                    if (messageText != null) {
                        // Генерируем позицию и создаем новое сообщение
                        var position = MessagePosition.generateRandomPosition(client);
                        var message = new Message(messageText, position, lifecycleManager.getTickCounter());
                        lifecycleManager.addMessage(message);
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
    
    /**
     * Возвращает менеджер жизненного цикла для доступа к активным сообщениям
     * @return менеджер жизненного цикла
     */
    public MessageLifecycleManager getLifecycleManager() {
        return lifecycleManager;
    }
} 