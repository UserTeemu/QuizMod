package io.github.tivj.quizmod.commands;

import io.github.tivj.quizmod.QuizMod;
import io.github.tivj.quizmod.question.Question;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;

public class QuizModCommand extends CommandBase {
    @Override
    public String getCommandName() {
        return "quizmod";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/quizmod (optional arguments: <on>, <off>)";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return -1;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length == 1 && args[0].equalsIgnoreCase("reload")) QuizMod.INSTANCE.loadQuestions();
        else if (args.length == 1 && args[0].equalsIgnoreCase("generate")) QuizMod.INSTANCE.generateConfig();
        else if (QuizMod.INSTANCE.config != null) {
            if (args.length == 0) {
                if (QuizMod.INSTANCE.config.questions.size() == 0)
                    Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(new ChatComponentText(I18n.format("quizmod.no_questions")));
                else {
                    QuizMod.scheduleTaskForNextTick(() -> QuizMod.INSTANCE.displayQuiz());
                }
            } else if (args.length == 1) {
                if (args[0].equalsIgnoreCase("on") || args[0].equalsIgnoreCase("off")) {
                    QuizMod.INSTANCE.config.enabled = args[0].equalsIgnoreCase("on");
                    QuizMod.INSTANCE.recalculateTimer();
                }
            } else if (args.length == 2) {
                boolean canAsk = args[0].equalsIgnoreCase("canask");
                if (canAsk || args[0].equalsIgnoreCase("dontask")) {
                    StringBuilder query = new StringBuilder();
                    for (int i = 1; i < args.length; i++) query.append(args[i]).append(" ");
                    query.setLength(query.length() - 1);

                    for (Question q : QuizMod.INSTANCE.config.questions) {
                        if (q.getQuestion().contains(query.toString())) q.setCanBeAsked(canAsk);
                    }
                }
            }
        } else Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(new ChatComponentText(I18n.format("quizmod.config_null")));
    }
}