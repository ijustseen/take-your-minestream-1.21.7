package takeyourminestream.modid;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import org.jetbrains.annotations.Nullable;

public class ModConfigScreen extends Screen {
    private TextFieldWidget channelNameField;
    private TextFieldWidget messageLifetimeField;
    private TextFieldWidget messageFallField;
    private TextFieldWidget viewAngleField;
    private TextFieldWidget maxFreezeDistanceField;
    private ButtonWidget freezingToggleButton;
    private final @Nullable Screen parent;
    private String initialChannelName;

    public ModConfigScreen() {
        this(null);
    }

    public ModConfigScreen(@Nullable Screen parent) {
        super(Text.of("Настройки MineStream"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int y = this.height / 4;
        int fieldWidth = 180;
        int fieldHeight = 20;
        int spacing = 28;
        TextRenderer textRenderer = this.textRenderer;

        // Сохраняем начальное значение канала
        initialChannelName = ModConfig.TWITCH_CHANNEL_NAME;

        // Поле для имени канала
        channelNameField = new TextFieldWidget(textRenderer, centerX - fieldWidth / 2, y, fieldWidth, fieldHeight, Text.of("Имя Twitch-канала"));
        channelNameField.setText(ModConfig.TWITCH_CHANNEL_NAME);
        channelNameField.setChangedListener(s -> ModConfig.TWITCH_CHANNEL_NAME = s);
        this.addDrawableChild(channelNameField);
        y += spacing;

        // Поле для времени жизни сообщения
        messageLifetimeField = new TextFieldWidget(textRenderer, centerX - fieldWidth / 2, y, fieldWidth, fieldHeight, Text.of("Время жизни сообщения (тики)"));
        messageLifetimeField.setText(String.valueOf(ModConfig.MESSAGE_LIFETIME_TICKS));
        messageLifetimeField.setChangedListener(s -> {
            try { ModConfig.MESSAGE_LIFETIME_TICKS = Integer.parseInt(s); } catch (NumberFormatException ignored) {}
        });
        this.addDrawableChild(messageLifetimeField);
        y += spacing;

        // Поле для времени падения
        messageFallField = new TextFieldWidget(textRenderer, centerX - fieldWidth / 2, y, fieldWidth, fieldHeight, Text.of("Время падения (тики)"));
        messageFallField.setText(String.valueOf(ModConfig.MESSAGE_FALL_TICKS));
        messageFallField.setChangedListener(s -> {
            try { ModConfig.MESSAGE_FALL_TICKS = Integer.parseInt(s); } catch (NumberFormatException ignored) {}
        });
        this.addDrawableChild(messageFallField);
        y += spacing;

        // Кнопка для переключения ENABLE_FREEZING_ON_VIEW
        freezingToggleButton = ButtonWidget.builder(Text.of(getFreezingButtonText()), btn -> {
            ModConfig.ENABLE_FREEZING_ON_VIEW = !ModConfig.ENABLE_FREEZING_ON_VIEW;
            btn.setMessage(Text.of(getFreezingButtonText()));
        }).dimensions(centerX - fieldWidth / 2, y, fieldWidth, fieldHeight).build();
        this.addDrawableChild(freezingToggleButton);
        y += spacing;

        // Поле для угла обзора
        viewAngleField = new TextFieldWidget(textRenderer, centerX - fieldWidth / 2, y, fieldWidth, fieldHeight, Text.of("Угол обзора (градусы)"));
        viewAngleField.setText(String.valueOf(ModConfig.VIEW_ANGLE_DEGREES));
        viewAngleField.setChangedListener(s -> {
            try { ModConfig.VIEW_ANGLE_DEGREES = Double.parseDouble(s); } catch (NumberFormatException ignored) {}
        });
        this.addDrawableChild(viewAngleField);
        y += spacing;

        // Поле для максимальной дистанции заморозки
        maxFreezeDistanceField = new TextFieldWidget(textRenderer, centerX - fieldWidth / 2, y, fieldWidth, fieldHeight, Text.of("Макс. дистанция заморозки (блоки)"));
        maxFreezeDistanceField.setText(String.valueOf(ModConfig.MAX_FREEZE_DISTANCE));
        maxFreezeDistanceField.setChangedListener(s -> {
            try { ModConfig.MAX_FREEZE_DISTANCE = Double.parseDouble(s); } catch (NumberFormatException ignored) {}
        });
        this.addDrawableChild(maxFreezeDistanceField);
        y += spacing;

        // Кнопка "Сохранить и выйти"
        this.addDrawableChild(ButtonWidget.builder(Text.of("Сохранить и выйти"), btn -> {
            // Если имя канала изменилось — переподключаем
            if (!ModConfig.TWITCH_CHANNEL_NAME.equals(initialChannelName)) {
                takeyourminestream.modid.TwitchManager.onChannelNameChanged(ModConfig.TWITCH_CHANNEL_NAME);
            }
            takeyourminestream.modid.ConfigManager.saveConfig();
            this.close();
        }).dimensions(centerX - fieldWidth / 2, y, fieldWidth, fieldHeight).build());
    }

    private String getFreezingButtonText() {
        return "Заморозка при взгляде: " + (ModConfig.ENABLE_FREEZING_ON_VIEW ? "ВКЛ" : "ВЫКЛ");
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        int centerX = this.width / 2;
        int y = this.height / 4 - 30;
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, centerX, y, 0xFFFFFF);
    }

    @Override
    public void close() {
        if (this.parent != null) {
            this.client.setScreen(this.parent);
        } else {
            this.client.setScreen(null);
        }
    }
} 