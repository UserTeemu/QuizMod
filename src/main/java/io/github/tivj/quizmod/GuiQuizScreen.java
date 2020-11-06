package io.github.tivj.quizmod;

import io.github.tivj.quizmod.question.Question;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

import java.io.IOException;

public class GuiQuizScreen extends GuiScreen {
    private final int margin = 16;
    private final Question question;
    private GuiTextField answerField;
    private GuiButton checkAnswerButton;
    private int baseY;

    public GuiQuizScreen(Question question) {
        this.question = question;
    }

    private int getWindowHeight() {
        return 9 + margin + 22 + margin + 20;
    }

    @Override
    public void initGui() {
        baseY = this.height / 2 - getWindowHeight() / 2;
        int tempY = baseY + 9 + margin;
        this.answerField = new GuiTextField(10, this.fontRendererObj, this.width / 2 - 75, tempY, 150, 20);
        this.answerField.setFocused(true);
        this.answerField.setCanLoseFocus(false);
        tempY += 22 + margin;
        this.buttonList.add(checkAnswerButton = new GuiButton(0, this.width / 2 - 75, tempY, 150, 20, I18n.format("quizmod.check")));
        super.initGui();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRendererObj, QuizMod.NAME, this.width / 2, 15, 222972);

        fontRendererObj.drawStringWithShadow(
            this.question.getQuestion(),
            this.width / 2F - fontRendererObj.getStringWidth(this.question.getQuestion()) / 2F,
            baseY,
            222972
        );

        this.answerField.drawTextBox();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public void updateScreen() {
        this.answerField.updateCursorCounter();
        super.updateScreen();
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == 28 || keyCode == 156) {
            checkAnswer();
        } else if (keyCode == 1) {
            this.mc.displayGuiScreen(null);
        } else {
            this.answerField.textboxKeyTyped(typedChar, keyCode);
            super.keyTyped(typedChar, keyCode);
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        this.answerField.mouseClicked(mouseX, mouseY, mouseButton);
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.equals(this.checkAnswerButton)) {
            checkAnswer();
        }
        super.actionPerformed(button);
    }

    private void checkAnswer() {
        boolean isCorrectAnswer = question.isCorrectAnswer(this.answerField.getText());

        IChatComponent component;

        if (isCorrectAnswer) component = new ChatComponentText(I18n.format("quizmod.correct"));
        else component = new ChatComponentText(I18n.format("quizmod.incorrect", question.getCorrectAnswers()));

        component.setChatStyle(component.getChatStyle().setColor(isCorrectAnswer ? EnumChatFormatting.GREEN : EnumChatFormatting.RED));
        this.mc.ingameGUI.getChatGUI().printChatMessage(component);

        this.mc.displayGuiScreen(null);
    }

    @Override
    public void onGuiClosed() {
        QuizMod.INSTANCE.recalculateTimer();
        super.onGuiClosed();
    }

    @Override
    public boolean doesGuiPauseGame() {
        return true;
    }
}
