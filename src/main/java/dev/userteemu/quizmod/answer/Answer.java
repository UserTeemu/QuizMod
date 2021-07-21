package dev.userteemu.quizmod.answer;

import com.google.gson.JsonElement;

public interface Answer {
    String getCorrectAnswers();
    JsonElement getAnswersInJson();
    String getPlaceholder();
    boolean isCorrectAnswer(String givenAnswer);
}
