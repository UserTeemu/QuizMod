package dev.userteemu.quizmod.management;

import dev.userteemu.quizmod.question.Question;

import java.util.HashSet;
import java.util.Set;

public class QuizModConfig {
    public Set<Question> questions = new HashSet<>();
    public boolean enabled = true;
    public int maxTicksUntilReminder = 1000 * 20;
    public int minTicksUntilReminder = 60 * 20;
    public int maxAnswerInputLength = 256;
}
