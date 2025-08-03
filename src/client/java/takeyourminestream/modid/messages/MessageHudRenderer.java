package takeyourminestream.modid.messages;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.text.OrderedText;
import takeyourminestream.modid.ModConfig;
import takeyourminestream.modid.config.MessageSpawnMode;

import java.util.ArrayList;
import java.util.List;

/**
 * Рендерер для отображения сообщений как HUD виджет в правом верхнем углу экрана
 */
public class MessageHudRenderer {
    private final MessageLifecycleManager lifecycleManager;
    private static final int MAX_DISPLAYED_MESSAGES = 5;
    private static final int MESSAGE_MARGIN = 10;
    private static final int MESSAGE_PADDING = 6;
    private static final int MESSAGE_SPACING = 2;
    private static final int BACKGROUND_COLOR = 0x80000000; // Полупрозрачный черный
    private static final int TEXT_COLOR = 0xFFFFFFFF; // Белый текст
    private static final int BASE_MAX_MESSAGE_WIDTH = 300;
    
    public MessageHudRenderer(MessageLifecycleManager lifecycleManager) {
        this.lifecycleManager = lifecycleManager;
        
        HudRenderCallback.EVENT.register((drawContext, tickDelta) -> {
            if (ModConfig.getMESSAGE_SPAWN_MODE() == MessageSpawnMode.HUD_WIDGET) {
                renderHudMessages(drawContext);
            }
        });
    }
    
    private void renderHudMessages(DrawContext drawContext) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.world == null) return;
        
        List<Message> activeMessages = lifecycleManager.getActiveMessages();
        if (activeMessages.isEmpty()) return;
        
        TextRenderer textRenderer = client.textRenderer;
        int screenWidth = client.getWindow().getScaledWidth();
        int screenHeight = client.getWindow().getScaledHeight();
        
        // Получаем последние сообщения для отображения
        List<Message> messagesToDisplay = getMessagesToDisplay(activeMessages);
        if (messagesToDisplay.isEmpty()) return;
        
        int currentY = MESSAGE_MARGIN;
        
        for (Message message : messagesToDisplay) {
            // Вычисляем альфа-канал на основе возраста сообщения
            float alpha = calculateMessageAlpha(message);
            if (alpha <= 0.0f) continue;
            
            // Применяем масштаб к максимальной ширине
            float scale = takeyourminestream.modid.ModConfig.getMESSAGE_SCALE().getScale();
            int maxMessageWidth = (int)(BASE_MAX_MESSAGE_WIDTH * scale);
            
            // Разбиваем текст на строки
            List<OrderedText> wrappedText = textRenderer.wrapLines(Text.of(message.getText()), maxMessageWidth - MESSAGE_PADDING * 2);
            
            // Вычисляем размеры панели
            int maxLineWidth = 0;
            for (OrderedText line : wrappedText) {
                int lineWidth = textRenderer.getWidth(line);
                if (lineWidth > maxLineWidth) {
                    maxLineWidth = lineWidth;
                }
            }
            
            // Применяем масштаб к размерам панели
            int scaledPadding = (int)(MESSAGE_PADDING * scale);
            int scaledFontHeight = (int)(textRenderer.fontHeight * scale);
            
            int panelWidth = (int)(maxLineWidth * scale) + scaledPadding * 2;
            int panelHeight = wrappedText.size() * scaledFontHeight + scaledPadding * 2;
            
            // Позиция панели (справа)
            int panelX = screenWidth - panelWidth - MESSAGE_MARGIN;
            int panelY = currentY;
            
            // Рендерим фон панели с альфа-каналом
            int backgroundColor = applyAlpha(BACKGROUND_COLOR, alpha);
            drawContext.fill(panelX, panelY, panelX + panelWidth, panelY + panelHeight, backgroundColor);
            
            // Рендерим текст (масштаб уже применен к размерам панели)
            int textColor = applyAlpha(TEXT_COLOR, alpha);
            int textY = panelY + scaledPadding;
            
            for (OrderedText line : wrappedText) {
                int textX = panelX + scaledPadding;
                drawContext.drawText(textRenderer, line, textX, textY, textColor, true);
                textY += scaledFontHeight;
            }
            
            currentY += panelHeight + (int)(MESSAGE_SPACING * scale);
        }
    }
    
    /**
     * Получает сообщения для отображения (последние N сообщений)
     */
    private List<Message> getMessagesToDisplay(List<Message> activeMessages) {
        List<Message> result = new ArrayList<>();
        
        // Берем последние сообщения (в обратном порядке)
        int startIndex = Math.max(0, activeMessages.size() - MAX_DISPLAYED_MESSAGES);
        for (int i = activeMessages.size() - 1; i >= startIndex; i--) {
            result.add(activeMessages.get(i));
        }
        
        return result;
    }
    
    /**
     * Вычисляет альфа-канал сообщения на основе его возраста
     */
    private float calculateMessageAlpha(Message message) {
        int tickCounter = lifecycleManager.getTickCounter();
        int age = message.getEffectiveAge(tickCounter);
        int lifetime = ModConfig.getMESSAGE_LIFETIME_TICKS();
        int fallTicks = ModConfig.getMESSAGE_FALL_TICKS();
        
        if (age < lifetime) {
            // Сообщение еще живо
            return 1.0f;
        } else if (age < lifetime + fallTicks) {
            // Сообщение исчезает
            float fallProgress = (float)(age - lifetime) / (float)fallTicks;
            return 1.0f - fallProgress;
        } else {
            // Сообщение исчезло
            return 0.0f;
        }
    }
    
    /**
     * Применяет альфа-канал к цвету
     */
    private int applyAlpha(int color, float alpha) {
        int originalAlpha = (color >> 24) & 0xFF;
        int newAlpha = (int)(originalAlpha * alpha);
        return (color & 0x00FFFFFF) | (newAlpha << 24);
    }
}