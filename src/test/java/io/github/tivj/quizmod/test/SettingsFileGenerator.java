package io.github.tivj.quizmod.test;

import io.github.tivj.quizmod.QuizMod;
import io.github.tivj.quizmod.answer.Answer;
import io.github.tivj.quizmod.answer.LiteralAnswer;
import io.github.tivj.quizmod.management.QuizModConfig;
import io.github.tivj.quizmod.question.Question;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class SettingsFileGenerator {
    public static void main(String[] args) throws IOException {
        QuizMod.INSTANCE.config = new QuizModConfig();
        Reader in = new FileReader("c.csv");
        CSVParser csv = CSVFormat.EXCEL.parse(in);

        List<CSVRecord> records = csv.getRecords();
        int sizeOfFirstLine = records.get(0).size()-1;
        boolean first = true;
        for (CSVRecord record : records) {
            if (first) {
                first = false;
                continue;
            }
            Answer[] answers = new Answer[sizeOfFirstLine];

            for (int i = 0; i < record.size()-1; i++)
                answers[i] = new LiteralAnswer("", false, record.get(i+1));

            QuizMod.INSTANCE.config.questions.add(
                new Question(
                    record.get(0),
                    true,
                    answers
                )
            );
        }

        String out = QuizMod.gson.toJson(QuizMod.INSTANCE.config);
        System.out.println(out);
        csv.close();
        in.close();

        FileUtils.writeStringToFile(new File("quizmod-questions.json"), out, StandardCharsets.UTF_8);
    }
}
