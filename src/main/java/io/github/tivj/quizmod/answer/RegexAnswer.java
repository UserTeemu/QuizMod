package io.github.tivj.quizmod.answer;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

import java.util.HashSet;
import java.util.regex.Pattern;

public class RegexAnswer implements Answer {
    public HashSet<Pattern> answerRegexes = new HashSet<>(); // allows to have multiple valid answers that can be matched
    private String simpleAnswer; // an explanation that can be understood by a human
    private String placeholder;

    public RegexAnswer(String answer, String simpleAnswer) {
        this(answer, simpleAnswer, "");
    }

    public RegexAnswer(HashSet<String> answers, String simpleAnswer) {
        this(answers, simpleAnswer, "");
    }

    public RegexAnswer(String answer, String simpleAnswer, String placeholder) {
        this.answerRegexes.add(Pattern.compile(answer));
        this.simpleAnswer = simpleAnswer;
        this.placeholder = placeholder;
    }

    public RegexAnswer(HashSet<String> answers, String simpleAnswer, String placeholder) {
        this.simpleAnswer = simpleAnswer;
        this.placeholder = placeholder;
        for (String answer : answers) {
            this.answerRegexes.add(Pattern.compile(answer));
        }
    }

    @Override
    public String getCorrectAnswers() {
        return simpleAnswer;
    }

    @Override
    public JsonElement getAnswersInJson() {
        HashSet<String> regexes = new HashSet<>();
        for (Pattern regex : this.answerRegexes) regexes.add(regex.pattern());
        return new Gson().toJsonTree(regexes);
    }

    @Override
    public String getPlaceholder() {
        return this.placeholder;
    }

    @Override
    public boolean isCorrectAnswer(String givenAnswer) {
        for (Pattern answerRegex : this.answerRegexes) {
            if (answerRegex.matcher(givenAnswer).matches()) return true;
        }
        return false;
    }
}
