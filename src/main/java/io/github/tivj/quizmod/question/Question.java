package io.github.tivj.quizmod.question;

import io.github.tivj.quizmod.answer.Answer;

import java.util.Arrays;
import java.util.List;

public class Question {
    private String question;
    private List<Answer> answers;
    public boolean canBeAsked;

    public Question(String question, boolean canBeAsked, Answer... answers) {
        this.question = question;
        this.answers = Arrays.asList(answers.clone());
        this.canBeAsked = canBeAsked;
    }

    public String getQuestion() {
        return this.question;
    }

    // Note: Each Answer object in a question has it's own text field for input. If you are looking to have multiple valid answers (but only 1 field), you can have the answers in the Answer object.
    public List<Answer> getAnswers() {
        return this.answers;
    }

    public boolean isCorrectAnswer(String givenAnswer) {
        for (Answer answer : this.answers)
            if (answer.isCorrectAnswer(givenAnswer)) return true;
        return false;
    }
}
