package takeyourminestream.modid.messages;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.text.OrderedText;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.MathHelper;
import org.joml.Matrix4f;

import java.util.List;
import takeyourminestream.modid.ModConfig;
import takeyourminestream.modid.messages.MessageSpawner;

public class MessageRenderer {

    public MessageRenderer() {
        WorldRenderEvents.AFTER_ENTITIES.register(context -> {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.player == null || client.world == null) return;

            MatrixStack matrices = context.matrixStack();
            TextRenderer textRenderer = client.textRenderer;
            VertexConsumerProvider consumers = context.consumers();

            List<MessageSpawner.MessageDisplay> activeMessages = MessageSpawner.getActiveMessages();

            for (MessageSpawner.MessageDisplay message : activeMessages) {
                matrices.push();

                int tickCounter = MessageSpawner.getTickCounter();
                int age = tickCounter - (int)message.spawnTick();
                
                // Учитываем замороженные тики при вычислении возраста
                if (ModConfig.ENABLE_FREEZING_ON_VIEW) {
                    age = Math.max(0, age - message.frozenTicks());
                }
                
                int fadeStart = ModConfig.MESSAGE_LIFETIME_TICKS;
                int fadeEnd = ModConfig.MESSAGE_LIFETIME_TICKS + ModConfig.MESSAGE_FADEOUT_TICKS;
                float alpha = 1.0f;
                if (age > fadeStart) {
                    float fadeProgress = (float)(age - fadeStart) / (float)ModConfig.MESSAGE_FADEOUT_TICKS;
                    alpha = 1.0f - Math.min(Math.max(fadeProgress, 0.0f), 1.0f);
                }

                // Translate to the message position
                matrices.translate(message.pos().getX() - client.gameRenderer.getCamera().getPos().getX(),
                                   message.pos().getY() - client.gameRenderer.getCamera().getPos().getY(),
                                   message.pos().getZ() - client.gameRenderer.getCamera().getPos().getZ());

                // Rotate to face the camera
                matrices.multiply(client.gameRenderer.getCamera().getRotation());

                // Scale the text down and flip Y for upright rendering
                matrices.scale(0.025f, -0.025f, 0.025f);

                // Word wrap the text
                List<OrderedText> wrappedText = textRenderer.wrapLines(Text.of(message.text()), 120); // 120 is approximately 3 blocks wide after scaling

                // Calculate total height of the wrapped text
                float totalTextHeight = wrappedText.size() * textRenderer.fontHeight;

                // Center the text AFTER scaling (relative to the scaled space)
                matrices.translate(-textRenderer.getWidth(wrappedText.get(0)) / 2.0f, -totalTextHeight / 2.0f, 0);

                // Render each line of the wrapped text
                for (int i = 0; i < wrappedText.size(); i++) {
                    int alphaInt = ((int)(255 * alpha)) << 24;
                    int color = (0xFFFFFF) | alphaInt;
                    textRenderer.draw(wrappedText.get(i),
                                      0.0F, // x
                                      (float)i * textRenderer.fontHeight, // y, adjusted for each line
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
        });
    }
} 