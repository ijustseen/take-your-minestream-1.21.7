package takeyourminestream.modid.messages;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.RenderLayer;
import org.joml.Matrix4f;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;

public class MessageParticleManager {
    private final List<MessageParticle> particles = new ArrayList<>();
    private static final Identifier PANEL_TEXTURE = Identifier.of("take-your-minestream", "textures/gui/message_panel.png");
    private static final Identifier PARTICLE_TEXTURE = Identifier.of("take-your-minestream", "textures/particles/particle_texture.png");

    public void addParticle(MessageParticle particle) {
        particles.add(particle);
    }

    public void addParticles(List<MessageParticle> newParticles) {
        particles.addAll(newParticles);
    }

    public void tick() {
        Iterator<MessageParticle> it = particles.iterator();
        while (it.hasNext()) {
            MessageParticle p = it.next();
            p.tick();
            if (!p.isAlive()) {
                it.remove();
            }
        }
    }

    public void render(MinecraftClient client, MatrixStack matrices, VertexConsumerProvider consumers) {
        if (particles.isEmpty()) return;
        matrices.push();
        for (MessageParticle p : particles) {
            float alpha = 1.0f;
            int r = p.color.getRed();
            int g = p.color.getGreen();
            int b = p.color.getBlue();
            float fr = r / 255.0f;
            float fg = g / 255.0f;
            float fb = b / 255.0f;
            // Переводим мировые координаты в локальные относительно камеры
            double camX = client.gameRenderer.getCamera().getPos().getX();
            double camY = client.gameRenderer.getCamera().getPos().getY();
            double camZ = client.gameRenderer.getCamera().getPos().getZ();
            double x = p.position.x - camX;
            double y = p.position.y - camY;
            double z = p.position.z - camZ;
            matrices.push();
            matrices.translate(x, y, z);
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-p.yaw));
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(p.pitch));
            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(p.rotation));
            // Квадрат всегда смотрит в том же направлении, что и сообщение
            // Размер в блоках
            float sz = p.size * 0.025f;
            // Получаем матрицу
            Matrix4f mat = matrices.peek().getPositionMatrix();
            VertexConsumer consumer = consumers.getBuffer(RenderLayer.getEntityTranslucent(PARTICLE_TEXTURE));
            int light = 0xF000F0;
            int overlay = 0;
            // TODO: поддержка цветных партиклов через .color(fr, fg, fb, alpha)
            // Сейчас всегда белый цвет, чтобы не было оттенков
            fr = 1f; fg = 1f; fb = 1f;
            // Вершины квадрата (XY-плоскость)
            consumer.vertex(mat, -sz/2, -sz/2, 0)
                    .color(fr, fg, fb, alpha)
                    .texture(0, 0)
                    .overlay(overlay)
                    .light(light)
                    .normal(0, 0, -1);
            consumer.vertex(mat, -sz/2, sz/2, 0)
                    .color(fr, fg, fb, alpha)
                    .texture(0, 1)
                    .overlay(overlay)
                    .light(light)
                    .normal(0, 0, -1);
            consumer.vertex(mat, sz/2, sz/2, 0)
                    .color(fr, fg, fb, alpha)
                    .texture(1, 1)
                    .overlay(overlay)
                    .light(light)
                    .normal(0, 0, -1);
            consumer.vertex(mat, sz/2, -sz/2, 0)
                    .color(fr, fg, fb, alpha)
                    .texture(1, 0)
                    .overlay(overlay)
                    .light(light)
                    .normal(0, 0, -1);
            matrices.pop();
        }
        matrices.pop();
    }

    public List<MessageParticle> getParticles() {
        return particles;
    }
} 