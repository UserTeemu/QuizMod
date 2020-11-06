package io.github.tivj.quizmod.question;

import com.google.gson.Gson;

import java.util.HashSet;
import java.util.regex.Pattern;

public class RegexQuestion implements Question {
    public String question;
    public HashSet<Pattern> answerRegexes = new HashSet<>();
    private String simpleAnswer; // an explanation that can be understood by a human
    private boolean canBeAsked;

    public RegexQuestion(String question, String answer, String simpleAnswer, boolean canBeAsked) {
        this.question = question;
        this.answerRegexes.add(Pattern.compile(answer));
        this.simpleAnswer = simpleAnswer;
        this.canBeAsked = canBeAsked;
    }

    public RegexQuestion(String question, HashSet<String> answers, String simpleAnswer, boolean canBeAsked) {
        this.question = question;
        this.simpleAnswer = simpleAnswer;
        this.canBeAsked = canBeAsked;
        for (String answer : answers) {
            this.answerRegexes.add(Pattern.compile(answer));
        }
    }

    @Override
    public String getQuestion() {
        return this.question;
    }

    @Override
    public String getCorrectAnswers() {
        return simpleAnswer;
    }

    @Override
    public String getAnswersInJson() {
        HashSet<String> regexes = new HashSet<>();
        for (Pattern regex : this.answerRegexes) regexes.add(regex.pattern());
        return new Gson().toJson(regexes);
    }

    @Override
    public boolean isCorrectAnswer(String givenAnswer) {
        for (Pattern answerRegex : this.answerRegexes) {
            if (answerRegex.matcher(givenAnswer).matches()) return true;
        }
        return false;
    }

    @Override
    public void setCanBeAsked(boolean status) {
        this.canBeAsked = status;
    }

    @Override
    public boolean canBeAsked() {
        return this.canBeAsked;
    }
}
