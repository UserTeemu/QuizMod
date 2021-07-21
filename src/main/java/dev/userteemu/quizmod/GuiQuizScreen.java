package dev.userteemu.quizmod;

import dev.userteemu.quizmod.answer.Answer;
import dev.userteemu.quizmod.question.Question;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GuiQuizScreen extends GuiScreen {
    private final int margin = 16;
    private final Question question;
    private final List<GuiTextField> answerFields = new ArrayList<>();
    private GuiButton checkAnswerButton;
    private int baseY;

    public GuiQuizScreen(Question question) {
        this.question = question;
    }

    private int getWindowHeight() {
        return 9 + margin + (22 + margin) * question.getAnswers().size() + 20;
    }

    @Override
    public void initGui() {
        baseY = this.height / 2 - getWindowHeight() / 2;
        int tempY = baseY + 9 + margin;
        int width = Math.min(this.width / 3, 200); // button textures break at widths higher than 200
        int x = this.width / 2 - width / 2;
        this.answerFields.clear();
        for (int i = 0; i < question.getAnswers().size(); i++) {
            GuiTextField answerField = new GuiTextField(10, this.fontRendererObj, x, tempY, width, 20);
            answerField.setText(this.question.getAnswers().get(i).getPlaceholder());
            answerField.setMaxStringLength(QuizMod.INSTANCE.config.maxAnswerInputLength);
            this.answerFields.add(answerField);
            tempY += 22 + margin;
        }
        this.answerFields.get(0).setFocused(true);
        this.buttonList.add(checkAnswerButton = new GuiButton(0, x, tempY, width, 20, I18n.format("quizmod.check")));
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

        for (GuiTextField answerField : answerFields) answerField.drawTextBox();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public void updateScreen() {
        for (GuiTextField answerField : answerFields) answerField.updateCursorCounter();
        super.updateScreen();
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == 28 || keyCode == 156 || keyCode == 15) {
            // if last field is focused, check answer, else move focus to the next field
            for (int i = 0; i < answerFields.size(); i++) {
                GuiTextField answerField = answerFields.get(i);
                if (answerField.isFocused()) {
                    if (i == answerFields.size() - 1) checkAnswer();
                    else {
                        answerField.setFocused(false);
                        answerFields.get(i+1).setFocused(true);
                    }
                    break;
                }
            }
        } else if (keyCode == 1) {
            this.mc.displayGuiScreen(null);
        } else {
            for (GuiTextField answerField : answerFields) answerField.textboxKeyTyped(typedChar, keyCode);
            super.keyTyped(typedChar, keyCode);
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        for (GuiTextField answerField : answerFields) answerField.mouseClicked(mouseX, mouseY, mouseButton);
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.equals(this.checkAnswerButton)) {
            checkAnswer();
        }
        super.actionPerformed(button);
    }

    public void checkAnswer() {
        IChatComponent component = new ChatComponentText("");
        if (this.answerFields.size() == this.question.getAnswers().size()) {
            if (this.answerFields.size() == 1) {
                GuiTextField answerField = this.answerFields.get(0);
                Answer answer = this.question.getAnswers().get(0);
                boolean isCorrectAnswer = answer.isCorrectAnswer(answerField.getText());

                if (isCorrectAnswer)
                    component = new ChatComponentText(I18n.format("quizmod.correct"));
                else {
                    component = new ChatComponentText(I18n.format("quizmod.incorrect", answer.getCorrectAnswers()));
                }
                component.setChatStyle(component.getChatStyle().setColor(isCorrectAnswer ? EnumChatFormatting.GREEN : EnumChatFormatting.RED));
            } else if (this.answerFields.size() > 1) {
                List<IChatComponent> siblings = new ArrayList<>();
                boolean allAreIncorrect = true;
                boolean allAreCorrect = true;
                for (int answerIndex = 0; answerIndex < this.question.getAnswers().size(); answerIndex++) {
                    GuiTextField answerField = this.answerFields.get(answerIndex);
                    Answer answer = this.question.getAnswers().get(answerIndex);

                    boolean isCorrectAnswer = answer.isCorrectAnswer(answerField.getText());

                    if (isCorrectAnswer) allAreIncorrect = false;
                    else allAreCorrect = false;

                    ChatComponentText subComponent;
                    if (isCorrectAnswer)
                        siblings.add(subComponent = new ChatComponentText(I18n.format("quizmod.multianswer.correct", answerIndex + 1, answer.getCorrectAnswers())));
                    else {
                        siblings.add(subComponent = new ChatComponentText(I18n.format("quizmod.multianswer.incorrect", answerIndex + 1, answerField.getText(), answer.getCorrectAnswers())));
                    }
                    subComponent.setChatStyle(subComponent.getChatStyle().setColor(isCorrectAnswer ? EnumChatFormatting.GREEN : EnumChatFormatting.RED));
                }

                component = new ChatComponentText(I18n.format(allAreCorrect ? "quizmod.all_correct" : allAreIncorrect ? "quizmod.all_incorrect" : "quizmod.partly_incorrect"));
                if (!allAreCorrect) {
                    for (IChatComponent sibling : siblings) {
                        component.appendSibling(new ChatComponentText("\n"));
                        component.appendSibling(sibling);
                    }
                }
            }
        }
        this.mc.ingameGUI.getChatGUI().printChatMessage(component);

        if (isShiftKeyDown()) QuizMod.INSTANCE.displayQuiz();
        else this.mc.displayGuiScreen(null);
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
