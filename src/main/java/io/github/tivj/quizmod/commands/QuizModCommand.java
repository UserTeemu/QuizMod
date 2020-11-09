package io.github.tivj.quizmod.commands;

import io.github.tivj.quizmod.QuizMod;
import io.github.tivj.quizmod.management.QuizModConfig;
import io.github.tivj.quizmod.answer.LiteralAnswer;
import io.github.tivj.quizmod.answer.RegexAnswer;
import io.github.tivj.quizmod.question.Question;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.util.ChatComponentText;

public class QuizModCommand extends CommandBase {
    @Override
    public String getCommandName() {
        return "quizmod";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "quizmod.command_usage";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return -1;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        if (args.length == 1 && args[0].equalsIgnoreCase("load")) QuizMod.INSTANCE.loadQuestions();
        else if (args.length == 1 && args[0].equalsIgnoreCase("save")) QuizMod.INSTANCE.generateConfig();
        else if (args.length == 1 && args[0].equalsIgnoreCase("testconfig")) createTestConfig();
        else if (QuizMod.INSTANCE.config != null) {
            if (args.length == 0) {
                if (QuizMod.INSTANCE.config.questions.size() == 0)
                    Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(new ChatComponentText(I18n.format("quizmod.no_questions")));
                else {
                    QuizMod.scheduleTaskForNextTick(() -> QuizMod.INSTANCE.displayQuiz());
                }
                return;
            } else if (args.length == 1) {
                if (args[0].equalsIgnoreCase("on") || args[0].equalsIgnoreCase("off")) {
                    QuizMod.INSTANCE.config.enabled = args[0].equalsIgnoreCase("on");
                    QuizMod.INSTANCE.recalculateTimer();
                    return;
                }
            } else if (args.length == 2) {
                boolean canAsk = args[0].equalsIgnoreCase("canask");
                if (canAsk || args[0].equalsIgnoreCase("dontask")) {
                    StringBuilder query = new StringBuilder();
                    for (int i = 1; i < args.length; i++) query.append(args[i]).append(" ");
                    query.setLength(query.length() - 1);

                    for (Question q : QuizMod.INSTANCE.config.questions) {
                        if (q.getQuestion().contains(query.toString())) q.canBeAsked = canAsk;
                    }
                    return;
                }
            }
            throw new WrongUsageException("quizmod.invalid_usage", getCommandUsage(sender));
        } else Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(new ChatComponentText(I18n.format("quizmod.config_null")));
    }

    private void createTestConfig() { // creates an example config
        QuizMod.INSTANCE.config = new QuizModConfig();

        QuizMod.INSTANCE.config.questions.add(
            new Question(
                "What determines if your answer to this question is correct?",
                true,
                new RegexAnswer(
                    "a? (regex|regular expression)",
                    "a regex or a regular expression"
                )
            )
        );

        QuizMod.INSTANCE.config.questions.add(
            new Question(
                "What is the capital of Finland?",
                true,
                // When there are 2 answer objects in the Question object (like now), both answers will have their own text input fields and both need to be answered.
                new LiteralAnswer(
                    "The name in Finnish and English",
                    false,
                    "Helsinki"
                ),
                new LiteralAnswer(
                    "The name in Swedish",
                    false,
                    "Helsingfors"
                )
            )
        );

        QuizMod.INSTANCE.config.questions.add(
            new Question(
                "Did I spend too much time making this mod?",
                true,
                // When there are 2 answers in the same Answer object, both are correct answers.
                new LiteralAnswer(
                    "",
                    false,
                    "Yes", "Absolutely" // Both "yes" and "absolutely" are correct answers.
                )
            )
        );
    }
}