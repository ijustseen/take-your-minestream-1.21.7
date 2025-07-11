package takeyourminestream.modid.messages;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import org.joml.Matrix4f;

import java.util.List;

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

                // Translate to the message position
                matrices.translate(message.pos().getX() - client.gameRenderer.getCamera().getPos().getX(),
                                   message.pos().getY() - client.gameRenderer.getCamera().getPos().getY(),
                                   message.pos().getZ() - client.gameRenderer.getCamera().getPos().getZ());

                // Rotate to face the camera
                matrices.multiply(client.gameRenderer.getCamera().getRotation());

                // Scale the text down and flip Y for upright rendering
                matrices.scale(0.025f, -0.025f, 0.025f);

                // Center the text AFTER scaling (relative to the scaled space)
                float textWidth = textRenderer.getWidth(Text.of(message.text()));
                matrices.translate(-textWidth / 2.0f, -textRenderer.fontHeight / 2.0f, 0);

                // Render the text
                textRenderer.draw(Text.of(message.text()),
                                  0.0F, // x
                                  0.0F, // y
                                  0xFFFFFFFF,
                                  true,
                                  matrices.peek().getPositionMatrix(),
                                  consumers,
                                  TextRenderer.TextLayerType.POLYGON_OFFSET,
                                  0,
                                  0xF000F0
                                  );

                matrices.pop();
            }
        });
    }
} 