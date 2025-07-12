package takeyourminestream.modid.messages;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.text.OrderedText;
import takeyourminestream.modid.ModConfig;
import net.minecraft.util.math.RotationAxis;

import java.util.List;

/**
 * Отвечает за рендеринг сообщений в мире
 */
public class MessageRenderer {
    private final MessageLifecycleManager lifecycleManager;
    
    public MessageRenderer(MessageLifecycleManager lifecycleManager) {
        this.lifecycleManager = lifecycleManager;
        
        WorldRenderEvents.AFTER_ENTITIES.register(context -> {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.player == null || client.world == null) return;

            MatrixStack matrices = context.matrixStack();
            TextRenderer textRenderer = client.textRenderer;
            VertexConsumerProvider consumers = context.consumers();

            List<Message> activeMessages = lifecycleManager.getActiveMessages();

            for (Message message : activeMessages) {
                renderMessage(client, message, matrices, textRenderer, consumers);
            }
        });
    }
    
    /**
     * Рендерит одно сообщение
     */
    private void renderMessage(MinecraftClient client, Message message, MatrixStack matrices, 
                             TextRenderer textRenderer, VertexConsumerProvider consumers) {
        matrices.push();

        int tickCounter = lifecycleManager.getTickCounter();
        int age = message.getEffectiveAge(tickCounter);
        
        // Вычисляем прозрачность на основе возраста
        float alpha = calculateAlpha(age);

        // Перемещаем к позиции сообщения
        matrices.translate(
            message.getPosition().getX() - client.gameRenderer.getCamera().getPos().getX(),
            message.getPosition().getY() - client.gameRenderer.getCamera().getPos().getY(),
            message.getPosition().getZ() - client.gameRenderer.getCamera().getPos().getZ()
        );

        // Поворачиваем к фиксированному направлению (yaw/pitch)
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-message.getYaw()));
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(message.getPitch()));

        // Масштабируем текст
        matrices.scale(0.025f, -0.025f, 0.025f);

        // Переносим строки
        List<OrderedText> wrappedText = textRenderer.wrapLines(Text.of(message.getText()), 120);

        // Вычисляем общую высоту текста
        float totalTextHeight = wrappedText.size() * textRenderer.fontHeight;

        // Центрируем текст
        matrices.translate(-textRenderer.getWidth(wrappedText.get(0)) / 2.0f, -totalTextHeight / 2.0f, 0);

        // Рендерим каждую строку
        for (int i = 0; i < wrappedText.size(); i++) {
            int alphaInt = ((int)(255 * alpha)) << 24;
            int color = (0xFFFFFF) | alphaInt;
            textRenderer.draw(wrappedText.get(i),
                              0.0F,
                              (float)i * textRenderer.fontHeight,
                             color,
                              true,
                              matrices.peek().getPositionMatrix(),
                              consumers,
                              TextRenderer.TextLayerType.POLYGON_OFFSET,
                              0,
                              0xF000F0
                              );
        }
        matrices.pop();
    }
    
    /**
     * Вычисляет прозрачность на основе возраста сообщения
     */
    private float calculateAlpha(int age) {
        int fadeStart = ModConfig.MESSAGE_LIFETIME_TICKS;
        int fadeEnd = ModConfig.MESSAGE_LIFETIME_TICKS + ModConfig.MESSAGE_FADEOUT_TICKS;
        
        if (age > fadeStart) {
            float fadeProgress = (float)(age - fadeStart) / (float)ModConfig.MESSAGE_FADEOUT_TICKS;
            return 1.0f - Math.min(Math.max(fadeProgress, 0.0f), 1.0f);
        }
        return 1.0f;
    }
} 