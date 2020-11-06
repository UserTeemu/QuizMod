package io.github.tivj.quizmod.question;

import com.google.gson.Gson;

import java.util.HashSet;

public class LiteralQuestion implements Question {
    public String question;
    public HashSet<String> answers;
    private boolean canBeAsked;

    public LiteralQuestion(String question, String answer, boolean canBeAsked) {
        this.question = question;
        this.answers = new HashSet<>();
        this.canBeAsked = canBeAsked;
        answers.add(answer);
    }

    public LiteralQuestion(String question, HashSet<String> answers, boolean canBeAsked) {
        this.question = question;
        this.answers = answers;
        this.canBeAsked = canBeAsked;
    }

    @Override
    public String getQuestion() {
        return this.question;
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
    public String getAnswersInJson() {
        return new Gson().toJson(this.answers);
    }

    @Override
    public boolean isCorrectAnswer(String givenAnswer) {
        for (String answer : this.answers) {
            if (answer.trim().equalsIgnoreCase(givenAnswer.trim())) return true;
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
