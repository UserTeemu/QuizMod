package io.github.tivj.quizmod.question;

public interface Question {
    String getQuestion();
    String getCorrectAnswers();
    String getAnswersInJson();
    boolean isCorrectAnswer(String givenAnswer);

    void setCanBeAsked(boolean status);
    boolean canBeAsked();
}
