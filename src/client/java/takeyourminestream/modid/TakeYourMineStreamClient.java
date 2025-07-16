package takeyourminestream.modid;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import com.mojang.brigadier.arguments.StringArgumentType;
import takeyourminestream.modid.messages.MessageSpawner;
import takeyourminestream.modid.messages.MessageSystemFactory;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

public class TakeYourMineStreamClient implements ClientModInitializer {

	private static MessageSpawner messageSpawner;
	private static KeyBinding openConfigScreenKeyBinding;
	private static KeyBinding twitchToggleKeyBinding; // Новый биндинг
	private static KeyBinding donationAlertsToggleKeyBinding;

	@Override
	public void onInitializeClient() {
		// Загружаем пользовательский конфиг
		ConfigManager.loadConfig();
		// Инициализируем систему сообщений через фабрику
		messageSpawner = MessageSystemFactory.createMessageSystem();

		// Регистрация KeyBinding для открытия экрана настроек
		openConfigScreenKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
			"Open Config",
			InputUtil.Type.KEYSYM,
			GLFW.GLFW_KEY_RIGHT_BRACKET, // Клавиша ']'
			"Take Your Minestream"
		));
		// Регистрация KeyBinding для Twitch toggle
		// (GLFW.GLFW_KEY_LEFT_BRACKET — это '[')
		twitchToggleKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
			"Toggle Twitch",
			InputUtil.Type.KEYSYM,
			GLFW.GLFW_KEY_LEFT_BRACKET, // Клавиша '['
			"Take Your Minestream"
		));
		// Регистрация KeyBinding для Donation Alerts toggle
		donationAlertsToggleKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
			"Toggle Donation Alerts",
			InputUtil.Type.KEYSYM,
			GLFW.GLFW_KEY_BACKSLASH, // Клавиша '\'
			"Take Your Minestream"
		));

		// Обработка нажатия клавиш
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (openConfigScreenKeyBinding.wasPressed()) {
				client.setScreen(new ModConfigScreen());
			}
			while (twitchToggleKeyBinding.wasPressed()) {
				if (TwitchManager.isConnected()) {
					TwitchManager.disconnect();
				} else {
					TwitchManager.connect(messageSpawner);
				}
			}
			while (donationAlertsToggleKeyBinding.wasPressed()) {
				if (DonationAlertsManager.isConnected()) {
					DonationAlertsManager.disconnect();
				} else {
					DonationAlertsManager.connect(messageSpawner);
				}
			}
		});

		// TwitchManager больше не создается автоматически!

		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
		ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
			dispatcher.register(ClientCommandManager.literal("minestream")
				.then(ClientCommandManager.literal("test")
					.then(ClientCommandManager.argument("message", StringArgumentType.greedyString())
						.executes(context -> {
							String message = StringArgumentType.getString(context, "message");
							messageSpawner.setCurrentMessage(message);
							if (MinecraftClient.getInstance().player != null) {
								MinecraftClient.getInstance().player.sendMessage(Text.of("Minestream message set to: " + message), false);
							}
							return 1;
						})))
				.then(ClientCommandManager.literal("stop")
					.executes(context -> {
						messageSpawner.setCurrentMessage("");
						if (MinecraftClient.getInstance().player != null) {
							MinecraftClient.getInstance().player.sendMessage(Text.of("Minestream message stopped."), false);
						}
						TwitchManager.disconnect();
						return 1;
					}))
				.then(ClientCommandManager.literal("twitch")
					.then(ClientCommandManager.literal("start")
						.executes(context -> {
							TwitchManager.connect(messageSpawner);
							return 1;
						}))
					.then(ClientCommandManager.literal("stop")
						.executes(context -> {
							TwitchManager.disconnect();
							return 1;
						})))
			);
		});
	}
}