package dev.userteemu.quizmod.management;

import com.google.gson.*;
import dev.userteemu.quizmod.answer.Answer;
import dev.userteemu.quizmod.answer.LiteralAnswer;
import dev.userteemu.quizmod.answer.RegexAnswer;
import dev.userteemu.quizmod.QuizMod;

import java.lang.reflect.Type;
import java.util.HashSet;

public class AnswerSerializer implements JsonSerializer<Answer>, JsonDeserializer<Answer> {
    @Override
    public JsonElement serialize(Answer answer, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject root = new JsonObject();
        root.addProperty("type", answer.getClass().getSimpleName());
        root.add("answers", answer.getAnswersInJson());
        root.addProperty("placeholder", answer.getPlaceholder());
        if (answer instanceof LiteralAnswer) root.addProperty("case_sensitive", ((LiteralAnswer) answer).isCaseSensitive());
        else if (answer instanceof RegexAnswer) root.addProperty("simple_answer", answer.getCorrectAnswers());
        return root;
    }

    @Override
    public Answer deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject root = json.getAsJsonObject();
        checkForComponents(root, "answers", "type");
        boolean hasPlaceholder = root.has("placeholder");
        String type = root.get("type").getAsString();
        switch (type) {
            case "LiteralAnswer":
                checkForComponents(root, "case_sensitive");
                return new LiteralAnswer(
                    hasPlaceholder ? root.get("placeholder").getAsString() : "",
                    root.get("case_sensitive").getAsBoolean(),
                    getAnswers(root.get("answers"))
                );
            case "RegexAnswer":
                checkForComponents(root, "simple_answer");
                return new RegexAnswer(
                    getAnswers(root.get("answers")),
                    root.get("simple_answer").getAsString(),
                    hasPlaceholder ? root.get("placeholder").getAsString() : ""
                );
            default:
                throw new IllegalStateException("Invalid answer type: "+type);
        }
    }

    private void checkForComponents(JsonObject root, String... members) {
        for (String member : members)
            if (!root.has(member)) throw new JsonParseException("JSON Element doesn't contain member \""+member+"\"! JSON Element:"+root.toString());
    }

    private HashSet<String> getAnswers(JsonElement answers) {
        return QuizMod.gson.fromJson(answers, HashSet.class);
    }
}
