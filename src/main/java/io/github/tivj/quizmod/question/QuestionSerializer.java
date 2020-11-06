package io.github.tivj.quizmod.question;

import com.google.gson.*;
import io.github.tivj.quizmod.QuizMod;

import java.lang.reflect.Type;
import java.util.HashSet;

public class QuestionSerializer implements JsonSerializer<Question>, JsonDeserializer<Question> {
    @Override
    public JsonElement serialize(Question question, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject root = new JsonObject();
        root.addProperty("type", question.getClass().getSimpleName());
        root.addProperty("can_be_asked", question.canBeAsked());
        root.addProperty("question", question.getQuestion());
        root.addProperty("answers", question.getAnswersInJson());
        if (question instanceof RegexQuestion) root.addProperty("simple_answer", question.getCorrectAnswers());
        return root;
    }

    @Override
    public Question deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject root = json.getAsJsonObject();
        checkForMembers(root, "question", "answers", "can_be_asked");
        String type = root.get("type").getAsString();
        switch (type) {
            case "LiteralQuestion":
                return new LiteralQuestion(
                    root.get("question").getAsString(),
                    getAnswers(root.get("answers").toString()),
                    root.get("can_be_asked").getAsBoolean()
                );
            case "RegexQuestion":
                checkForMembers(root, "simple_answer");
                return new RegexQuestion(
                    root.get("question").getAsString(),
                    getAnswers(root.get("answers").toString()),
                    root.get("simple_answer").getAsString(),
                    root.get("can_be_asked").getAsBoolean()
                );
            case "MultiAnswerQuestion":
                return new RegexQuestion(
                    root.get("question").getAsString(),
                    getAnswers(root.get("answers").toString()),
                    root.get("simple_answer").getAsString(),
                    root.get("can_be_asked").getAsBoolean()
                );
            default:
                throw new IllegalStateException("Invalid question type: "+type);
        }
    }

    private HashSet<String> getAnswers(String answers) {
        return QuizMod.gson.fromJson(answers, HashSet.class);
    }

    private void checkForMembers(JsonObject root, String... memberNames) throws JsonParseException {
        for (String memberName : memberNames)
            if (!root.has(memberName))
                throw new JsonParseException("JSON Element doesn't contain member \""+memberName+"\"! JSON Element:");
    }
}
