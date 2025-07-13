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

	private static TwitchChatClient twitchChatClient;
	private static MessageSpawner messageSpawner;
	private static KeyBinding openConfigScreenKeyBinding;
	private static String lastTwitchChannelName = ModConfig.TWITCH_CHANNEL_NAME;
	private static boolean twitchConnected = false;

	public static void onTwitchChannelNameChanged(String newChannelName) {
		if (twitchConnected && !newChannelName.equals(lastTwitchChannelName)) {
			// Переподключаемся к новому каналу
			if (twitchChatClient != null) {
				twitchChatClient.disconnect();
			}
			twitchChatClient = new TwitchChatClient(newChannelName, messageSpawner);
			lastTwitchChannelName = newChannelName;
			if (MinecraftClient.getInstance().player != null) {
				MinecraftClient.getInstance().player.sendMessage(Text.of("Переподключено к Twitch-каналу: " + newChannelName), false);
			}
		}
		lastTwitchChannelName = newChannelName;
	}

	@Override
	public void onInitializeClient() {
		// Инициализируем систему сообщений через фабрику
		messageSpawner = MessageSystemFactory.createMessageSystem();

		// Регистрация KeyBinding для открытия экрана настроек
		openConfigScreenKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
			"key.takeyourminestream.open_config_screen",
			InputUtil.Type.KEYSYM,
			GLFW.GLFW_KEY_RIGHT_BRACKET, // Клавиша ']'
			"category.takeyourminestream"
		));

		// Обработка нажатия клавиши
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (openConfigScreenKeyBinding.wasPressed()) {
				client.setScreen(new ModConfigScreen());
			}
		});

		// TwitchChatClient больше не создается автоматически!

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
						if (twitchChatClient != null) {
							twitchChatClient.disconnect();
							twitchChatClient = null;
							twitchConnected = false;
						}
						return 1;
					}))
				.then(ClientCommandManager.literal("twitch")
					.then(ClientCommandManager.literal("start")
						.executes(context -> {
							if (twitchChatClient == null) {
								MinecraftClient.getInstance().player.sendMessage(Text.of("Connecting to Twitch chat '" + ModConfig.TWITCH_CHANNEL_NAME + "'..."), false);
								twitchChatClient = new TwitchChatClient(ModConfig.TWITCH_CHANNEL_NAME, messageSpawner);
								twitchConnected = true;
								lastTwitchChannelName = ModConfig.TWITCH_CHANNEL_NAME;
								if (MinecraftClient.getInstance().player != null) {
									MinecraftClient.getInstance().player.sendMessage(Text.of("Successfully connected!"), false);
								}
							} else {
								if (MinecraftClient.getInstance().player != null) {
									MinecraftClient.getInstance().player.sendMessage(Text.of("Already connected to Twitch chat."), false);
								}
							}
							return 1;
						}))
					.then(ClientCommandManager.literal("stop")
						.executes(context -> {
							if (twitchChatClient != null) {
								twitchChatClient.disconnect();
								twitchChatClient = null;
								twitchConnected = false;
								if (MinecraftClient.getInstance().player != null) {
									MinecraftClient.getInstance().player.sendMessage(Text.of("Disconnected from Twitch chat."), false);
								}
							} else {
								if (MinecraftClient.getInstance().player != null) {
									MinecraftClient.getInstance().player.sendMessage(Text.of("Not connected to Twitch chat."), false);
								}
							}
							return 1;
						})))
			);
		});
	}
}