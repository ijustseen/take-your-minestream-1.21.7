# Take Your MineStream Mod

A Minecraft mod that displays custom messages directly in the game world. It allows players to set arbitrary text messages and automatically receives and displays messages from Twitch chat.

## Features

### Message Display System

- **3D Text Rendering**: Messages are rendered as 3D text in the game world with customizable positioning
- **Dynamic Sizing**: Message size is calculated based on text content and automatically wrapped
- **Background Panel**: Messages have a beautiful 9-slice background panel for better visibility
- **Lifecycle Management**: Messages have configurable lifetime, fall animation, and freezing mechanics
- **View-Based Freezing**: Messages can be frozen (prevented from disappearing) when the player looks at them

### Twitch Integration

- **Real-time Chat**: Automatically connects to Twitch IRC and displays messages in-game
- **Direct IRC Connection**: Uses native Java sockets for reliable Twitch chat connection
- **Configurable Channel**: Set your Twitch channel name in the configuration
- **Message Queue**: Prevents message spam with intelligent queuing system
- **Connection Management**: Start/stop Twitch connection with in-game commands

### Configuration

- **In-Game Settings**: Access configuration screen with key binding (default: `]`)
- **Twitch Toggle**: Toggle Twitch connection with key binding (default: `[`)
- **Customizable Parameters**:
  - Message lifetime and fall duration
  - Maximum freeze distance
  - Spawn mode (around player or in front only)
  - Freezing on view toggle

## Commands

The mod provides the `/minestream` command with the following subcommands:

- `/minestream test <message>`: Sets a test message that will be displayed in the game
- `/minestream stop`: Clears the current message and disconnects from Twitch chat
- `/minestream twitch start`: Connects to Twitch chat for the configured channel
- `/minestream twitch stop`: Disconnects from Twitch chat

## Key Bindings

- **`]` (Right Bracket)**: Open configuration screen
- **`[` (Left Bracket)**: Toggle Twitch connection

## Installation

1. Download the mod JAR file
2. Place it in your Minecraft `mods` folder
3. Start the game with Fabric Loader
4. Configure your Twitch channel name in the mod settings

## Configuration

### Accessing Settings

Press `]` (right bracket) in-game to open the configuration screen, or use the command `/minestream test` to trigger the settings.

### Available Settings

- **Twitch Channel Name**: The channel to connect to for chat messages
- **Message Lifetime**: How long messages stay visible (in ticks)
- **Message Fall Duration**: How long the fall animation lasts (in ticks)
- **Freezing on View**: Whether messages freeze when player looks at them
- **Max Freeze Distance**: Maximum distance for view-based freezing
- **Spawn Mode**: Whether messages spawn around player or only in front

## Technical Details

### Architecture

The mod uses a modular architecture with clear separation of concerns:

- **Message System**: Handles message lifecycle, rendering, and positioning
- **Twitch Integration**: Manages Twitch chat connection and message processing
- **Configuration**: Provides in-game settings and persistence
- **View Detection**: Advanced ray-plane intersection for accurate view detection

### Message Rendering

- Uses Minecraft's text renderer with custom 3D positioning
- Implements 9-slice background panels for professional appearance
- Supports text wrapping and dynamic sizing
- Includes fall animation with quadratic easing

### View Detection System

The mod uses advanced ray-plane intersection to determine if a player is looking at a message:

1. Calculates effective message age (accounting for freezing)
2. Computes fall offset using the same logic as rendering
3. Determines message dimensions based on text content
4. Checks ray intersection with the message plane
5. Verifies intersection point is within message bounds

## Development

### Building from Source

```bash
./gradlew build
```

### Project Structure

```
src/
├── client/java/takeyourminestream/modid/
│   ├── messages/           # Message system components
│   │   ├── Message.java
│   │   ├── MessageRenderer.java
│   │   ├── MessageViewDetector.java
│   │   ├── MessageLifecycleManager.java
│   │   ├── MessageSpawner.java
│   │   ├── MessageQueue.java
│   │   ├── MessagePosition.java
│   │   └── MessageSystemFactory.java
│   ├── TwitchChatClient.java
│   ├── TwitchManager.java
│   ├── ModConfig.java
│   ├── ModConfigScreen.java
│   ├── ConfigManager.java
│   └── TakeYourMineStreamClient.java
└── main/java/takeyourminestream/modid/
    └── TakeYourMineStream.java
```

### Dependencies

- **Fabric API**: For mod framework and rendering
- **Gson**: For configuration persistence
- **Java Sockets**: For direct Twitch IRC connection (no external Twitch library required)

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Test thoroughly
5. Submit a pull request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Support

If you encounter any issues or have questions, please open an issue on the project's GitHub page.

---

**Note**: This mod requires Fabric Loader and is compatible with Minecraft 1.21.7.
