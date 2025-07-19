package takeyourminestream.modid.input;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import takeyourminestream.modid.interfaces.ITwitchManager;
import takeyourminestream.modid.ModConfigScreen;
import takeyourminestream.modid.messages.MessageSpawner;
import takeyourminestream.modid.utils.Logger;

/**
 * Менеджер привязок клавиш
 */
public class KeyBindingManager {
    private final ITwitchManager twitchManager;
    private final MessageSpawner messageSpawner;
    private KeyBinding openConfigScreenKeyBinding;
    private KeyBinding twitchToggleKeyBinding;

    public KeyBindingManager(ITwitchManager twitchManager, MessageSpawner messageSpawner) {
        this.twitchManager = twitchManager;
        this.messageSpawner = messageSpawner;
    }

    public void registerKeyBindings() {
        // Регистрация KeyBinding для открытия экрана настроек
        openConfigScreenKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.takeyourminestream.openconfig",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_RIGHT_BRACKET, // Клавиша ']'
            "category.takeyourminestream.general"
        ));

        // Регистрация KeyBinding для Twitch toggle
        twitchToggleKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.takeyourminestream.toggletwitch",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_LEFT_BRACKET, // Клавиша '['
            "category.takeyourminestream.general"
        ));

        // Обработка нажатия клавиш
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openConfigScreenKeyBinding.wasPressed()) {
                handleOpenConfigScreen();
            }
            while (twitchToggleKeyBinding.wasPressed()) {
                handleTwitchToggle();
            }
        });
    }

    private void handleOpenConfigScreen() {
        try {
            net.minecraft.client.MinecraftClient.getInstance().setScreen(new ModConfigScreen());
            Logger.info("Открыт экран настроек");
        } catch (Exception e) {
            Logger.error("Ошибка при открытии экрана настроек", e);
            Logger.sendErrorToPlayer("Ошибка при открытии экрана настроек");
        }
    }

    private void handleTwitchToggle() {
        try {
            if (twitchManager.isConnected()) {
                twitchManager.disconnect();
            } else {
                twitchManager.connect(messageSpawner);
            }
        } catch (Exception e) {
            Logger.error("Ошибка при переключении Twitch", e);
            Logger.sendErrorToPlayer("Ошибка при переключении Twitch");
        }
    }

    public KeyBinding getOpenConfigScreenKeyBinding() {
        return openConfigScreenKeyBinding;
    }

    public KeyBinding getTwitchToggleKeyBinding() {
        return twitchToggleKeyBinding;
    }
} 