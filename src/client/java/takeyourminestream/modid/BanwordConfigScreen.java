package takeyourminestream.modid;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import takeyourminestream.modid.interfaces.IBanwordManager;

import java.util.ArrayList;
import java.util.List;

public class BanwordConfigScreen extends Screen {
    private final @Nullable Screen parent;
    private IBanwordManager banwordManager;
    private TextFieldWidget inputField;
    private int scrollOffset = 0;
    private static final int LINE_HEIGHT = 14;
    private static final int PADDING = 10;
    private final List<String> banwordList = new ArrayList<>();

    public BanwordConfigScreen(@Nullable Screen parent) {
        super(Text.translatable("takeyourminestream.banwords.title"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        this.banwordManager = TakeYourMineStreamClient.getInstance().getBanwordManager();
        reloadList();

        TextRenderer tr = this.textRenderer;

        int centerX = this.width / 2;
        int bottomY = this.height - 30;
        int buttonW = 100;
        int buttonH = 20;
        int spacing = 10;

        inputField = new TextFieldWidget(tr, PADDING, bottomY - buttonH - spacing, this.width - PADDING * 2 - buttonW - spacing, buttonH, Text.translatable("takeyourminestream.banwords.input"));
        this.addDrawableChild(inputField);

        this.addDrawableChild(ButtonWidget.builder(Text.translatable("takeyourminestream.banwords.add"), btn -> {
            String word = inputField.getText();
            if (word != null && !word.trim().isEmpty()) {
                banwordManager.addBanword(word.trim());
                inputField.setText("");
                reloadList();
            }
        }).dimensions(this.width - PADDING - buttonW, bottomY - buttonH - spacing, buttonW, buttonH).build());

        this.addDrawableChild(ButtonWidget.builder(Text.translatable("gui.back"), btn -> this.close()).dimensions(centerX - buttonW / 2, bottomY, buttonW, buttonH).build());
    }

    private void reloadList() {
        banwordList.clear();
        banwordList.addAll(banwordManager.getBanwords());
        banwordList.sort(String::compareTo);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        // Заголовок
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 10, 0xFFFFFF);

        // Фон списка
        int top = 40;
        int bottom = this.height - 60;
        context.fill(PADDING, top, this.width - PADDING, bottom, 0x40000000);

        int y = top + PADDING - scrollOffset;
        int removeButtonW = 60;
        int textX = PADDING * 2;
        for (int i = 0; i < banwordList.size(); i++) {
            String word = banwordList.get(i);
            if (y + LINE_HEIGHT > top + PADDING && y < bottom - PADDING) {
                context.drawText(this.textRenderer, Text.of(word), textX, y, 0xFFFFFF, true);
                int btnX = this.width - PADDING - removeButtonW;
                int btnY = y - 2;
                // Ленивая отрисовка/создание кнопок: перерисовывать не будем, вместо этого удаление по клику мыши
            }
            y += LINE_HEIGHT;
        }

        // Примитивный «клик для удаления» по правой части строки
        // Мы не создаём много кнопок, чтобы не перегружать экран.
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            int top = 40;
            int bottom = this.height - 60;
            int y = top + PADDING - scrollOffset;
            int removeButtonW = 60;
            int btnX = this.width - PADDING - removeButtonW;
            for (int i = 0; i < banwordList.size(); i++) {
                if (y + LINE_HEIGHT > top + PADDING && y < bottom - PADDING) {
                    if (mouseX >= btnX && mouseX <= btnX + removeButtonW && mouseY >= y - 2 && mouseY <= y - 2 + LINE_HEIGHT) {
                        String word = banwordList.get(i);
                        banwordManager.removeBanword(word);
                        reloadList();
                        return true;
                    }
                }
                y += LINE_HEIGHT;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        int top = 40;
        int bottom = this.height - 60;
        int totalHeight = banwordList.size() * LINE_HEIGHT;
        int visible = bottom - top - PADDING * 2;
        int maxScroll = Math.max(0, totalHeight - visible);
        scrollOffset = Math.max(0, Math.min(maxScroll, scrollOffset - (int)(verticalAmount * 20)));
        return true;
    }

    @Override
    public void close() {
        if (this.parent != null) {
            MinecraftClient.getInstance().setScreen(this.parent);
        } else {
            MinecraftClient.getInstance().setScreen(null);
        }
    }
}


