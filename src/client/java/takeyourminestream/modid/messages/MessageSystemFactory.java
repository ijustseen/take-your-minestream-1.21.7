package takeyourminestream.modid.messages;

/**
 * Фабрика для создания и инициализации системы сообщений
 */
public class MessageSystemFactory {
    
    /**
     * Создает и инициализирует все компоненты системы сообщений
     * @return настроенный MessageSpawner
     */
    public static MessageSpawner createMessageSystem() {
        MessageQueue messageQueue = new MessageQueue();
        MessageLifecycleManager lifecycleManager = new MessageLifecycleManager();
        MessageSpawner messageSpawner = new MessageSpawner(messageQueue, lifecycleManager);
        new MessageRenderer(lifecycleManager);
        
        return messageSpawner;
    }
} 