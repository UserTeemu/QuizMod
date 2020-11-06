package io.github.tivj.quizmod;

import io.github.tivj.quizmod.question.Question;

import java.util.HashSet;
import java.util.Set;

public class QuizModConfig {
    public Set<Question> questions = new HashSet<>();
    public boolean enabled = true;
    public int maxTicksUntilQuestion = 1000 * 20;
    public int minTicksUntilQuestion = 60 * 20;
}
