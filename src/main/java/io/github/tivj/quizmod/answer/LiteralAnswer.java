package io.github.tivj.quizmod.answer;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

import java.util.Arrays;
import java.util.HashSet;

public class LiteralAnswer implements Answer {
    public HashSet<String> answers; // allows to have multiple valid answers that can be matched
    private boolean caseSensitive;
    private String placeholder;

    public LiteralAnswer(String placeholder, boolean caseSensitive, String... answers) {
        this(placeholder, caseSensitive, new HashSet<>(Arrays.asList(answers)));
    }

    public LiteralAnswer(String placeholder, boolean caseSensitive, HashSet<String> answers) {
        this.answers = answers;
        this.caseSensitive = caseSensitive;
        this.placeholder = placeholder;
    }

    @Override
    public String getCorrectAnswers() {
        StringBuilder builder = new StringBuilder();
        for (String answer : this.answers) {
            builder.append(answer).append(", ");
        }
        return builder.substring(0, builder.length() - 2);
    }

    @Override
    public JsonElement getAnswersInJson() {
        return new Gson().toJsonTree(this.answers);
    }

    @Override
    public String getPlaceholder() {
        return this.placeholder;
    }

    public boolean isCaseSensitive() {
        return caseSensitive;
    }

    @Override
    public boolean isCorrectAnswer(String givenAnswer) {
        for (String answer : this.answers) {
            if (this.caseSensitive && answer.trim().equals(givenAnswer.trim())) return true;
            else if (answer.trim().equalsIgnoreCase(givenAnswer.trim())) return true;
        }
        return false;
    }
}
