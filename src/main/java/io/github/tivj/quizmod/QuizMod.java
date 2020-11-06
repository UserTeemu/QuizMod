package io.github.tivj.quizmod;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.github.tivj.quizmod.commands.QuizModCommand;
import io.github.tivj.quizmod.question.Question;
import io.github.tivj.quizmod.question.QuestionSerializer;
import net.minecraft.client.Minecraft;
import net.minecraft.event.ClickEvent;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Mod(modid = QuizMod.MODID, name = QuizMod.NAME, version = QuizMod.VERSION, clientSideOnly = true)
public class QuizMod {
    public static final String MODID = "quizmod";
    public static final String NAME = "QuizMod";
    public static final String VERSION = "@MOD_VERSION@"; // this will be replaced by Gradle
    public static final Logger LOGGER = LogManager.getLogger("QuizMod");

    @Mod.Instance(MODID)
    public static QuizMod INSTANCE;
    public static Gson gson = new GsonBuilder().registerTypeAdapter(Question.class, new QuestionSerializer()).create();

    public QuizModConfig config;
    public int ticksUntilNextQuestion = -1;

    public QuizMod() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        ClientCommandHandler.instance.registerCommand(new QuizModCommand());
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        loadQuestions();
    }

    public void loadQuestions() {
        File questionsFile = new File(Minecraft.getMinecraft().mcDataDir, "quizmod-questions.json");
        if (questionsFile.exists() && questionsFile.canRead()) {
            String questionsFileContents;
            try {
                questionsFileContents = FileUtils.readFileToString(questionsFile, StandardCharsets.UTF_8);
            } catch (IOException e) {
                e.printStackTrace();
                LOGGER.warn("Failed to read the questions!");
                return;
            }

            this.config = gson.fromJson(questionsFileContents, QuizModConfig.class);
            recalculateTimer();
            LOGGER.info("Successfully loaded the questions.");
        } else LOGGER.warn("Cannot read the questions!");
    }

    public boolean generateConfig() {
        if (config == null) this.config = new QuizModConfig();
        File questionsFile = new File(Minecraft.getMinecraft().mcDataDir, "quizmod-questions.json");

        if (!questionsFile.exists()) {
            try {
                questionsFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                LOGGER.warn("Could not create config file!");
                return false;
            }
        }

        if (questionsFile.canWrite()) {
            try {
                FileUtils.writeStringToFile(questionsFile, gson.toJson(this.config), StandardCharsets.UTF_8);
            } catch (IOException e) {
                e.printStackTrace();
                LOGGER.warn("Failed to write the config!");
                return false;
            }

            LOGGER.info("Successfully generated the config.");
            return true;
        } else {
            LOGGER.warn("Cannot write the config!");
            return false;
        }
    }

    public void recalculateTimer() {
        if (this.config == null || this.config.maxTicksUntilQuestion <= this.config.minTicksUntilQuestion) return;
        if (this.config.enabled) this.ticksUntilNextQuestion = new Random().nextInt(this.config.maxTicksUntilQuestion - this.config.minTicksUntilQuestion) + this.config.minTicksUntilQuestion;
        else this.ticksUntilNextQuestion = -1;
    }

    @SubscribeEvent
    public void clientTick(TickEvent.ClientTickEvent event) {
        if (this.config != null) {
            if (ticksUntilNextQuestion != -1) {
                if (ticksUntilNextQuestion == 0) {
                    sendNotification();
                    recalculateTimer();
                } else ticksUntilNextQuestion--;
            }

            if (toDoOnNextTick != null) {
                toDoOnNextTick.run();
                toDoOnNextTick = null;
            }
        }
    }

    public void sendNotification() {
        IChatComponent component = new ChatComponentTranslation("quizmod.notification");
        component.setChatStyle(component.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/quizmod")));
        Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(component);
    }

    public void displayQuiz() {
        int questions = 0;
        for (Question question : this.config.questions) if (question.canBeAsked()) questions++;
        int random = new Random().nextInt(questions);
        for (Question question : this.config.questions) {
            if (!question.canBeAsked()) continue;
            if (random == 0) {
                Minecraft.getMinecraft().displayGuiScreen(new GuiQuizScreen(question));
                return;
            }
            random--;
        }
    }

    private static Runnable toDoOnNextTick = null;
    public static void scheduleTaskForNextTick(Runnable runnable) {
        toDoOnNextTick = runnable;
    }
}