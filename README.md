# QuizMod
QuizMod is a customizable Minecraft mod that allows you to take simple quizzes while playing Minecraft.  
You can specify what questions you want to be asked and what are the correct answers and QuizMod will ask them and let you know if you gave a wrong answer.

QuizMod also reminds you to answer a question now and then (time range can be customized).  
The frequency of the reminders can be changed in the config.

## How to use
Throw the mod's jar file to your mods folder and create a configuration JSON to `gamedir/config/quizmod-questions.json`. For help with creating the config, see [creating a config section](#creating-a-config).

To answer the questions, use `/quizmod` in game, or let the mod remind you.

Commands that can be used:

| Command                       | Description                                                                              |
| ----------------------------- | ---------------------------------------------------------------------------------------- |
| `/quizmod`                    | Opens the GUI where you can answer a randomly selected question.                         |
| `/quizmod save`               | Saves the current config to the config file.                                             |
| `/quizmod load`               | Reloads the config and questions from the config file.                                   |
| `/quizmod off`                | Turns off the reminders. (Remember to save!)                                             |
| `/quizmod on`                 | Turns on the reminders. (Remember to save!) Enabled by default.                          |
| `/quizmod dontask` + question | Doesn't ask a question anymore. Must be followed by a part of the question.              |
| `/quizmod canask` + question  | Allows asking a question. Must be followed by a part of the question.                    |
| `/quizmod testconfig`         | Loads a test config. Current config will be lost unless it has been saved and is loaded. |

All changes to settings or commands are discarded when loading the config next time, if the changes haven't been saved with `/quizmod save`.

## Creating a config
What is the purpose of the config?  
It holds the settings for the mod and most importantly: the questions.

For info about the settings, see [Setting the Settings](#setting-the-settings)

### Question objects
Before answering a question, a random question object is drawn from the list of questions.  
A question can have multiple required answers.
Unlike answers, there is only 1 type of question.

There are 3 JSON properties of every question:
- `question` = the given question string for the user (string)
- `answers` = an array of the answer objects that tell the valid answers
- `can_be_asked` = if the question can be randomly selected to be asked (true or false)

### Answer objects
Answer object's job is to tell what is correct and what is wrong.

An answer object is assigned to every question (unless you want impossible questions). You can also have multiple answers for a single question.  
Each answer object assigned to a question will have its own input field.

However you might be interested in having multiple correct answers for 1 input field. Then you can assign multiple correct values to a single **answer object** (not the question object). Please check out the "Did I spend too much time making this mod?" example.

There are 2 types of answers: Literal answers (`LiteralAnswer`) and Regex answers (`RegexAnswer`). Each answer object's type must be specified in the JSON with the `type` property.

There are 2 JSON properties of every answer:
- `type` = type of the answer (`LiteralAnswer` or `RegexAnswer`)
- `answers` = an array of the valid answers (strings for literal answers or regexes in strings for regex answers)

#### Literal answers
Checks if the given answer has the same chars as the correct answer.  
Literal answers' behavior can be changed to ignore capitalisation differences with the `case_sensitive` property (see examples).

#### Regex answers
Checks if the given answer matches a specified regular expression.
When the answer to the question needs to be printed, QuizMod doesn't print the raw regex.  
Instead, a human understandable answer needs to be specified with the `simple_answer` property.

## Setting the Settings
Refer to the [example config](#example-config) to see how there are set.

| Option's name in JSON      | Description                            | Default value     | Value (x) limitations                                         |
| -------------------------- | -------------------------------------- | ----------------- | ------------------------------------------------------------- |
| `enabled`                  | If the mod is enabled or disabled      | true              | `x = false` or `x = true`                                     |
| `max_ticks_until_reminder` | Unit = game ticks, 20 ticks = 1 second | 1000 * 20 = 20000 | `x < 2147483647`, `x > 0` and x `>= min ticks until reminder` |
| `max_ticks_until_reminder` | Unit = game ticks, 20 ticks = 1 second | 60 * 20 = 12000   | `x < 2147483647`, `x > 0` and x `<= max ticks until reminder` |
| `max_answer_input_length ` | Max length of an answer.               | 256               | `x > 0`, and `x < 2147483647`                                 |
## Example config
Can also be generated by running `/quizmod testconfig` and `/quizmod save`. Note that this will override your existing config.

This config was generated in version 1.1.0.

You can find screenshots of how the questions appear in [screenshots of example config](#screenshots-of-example-config)
```json
{
  "questions": [
    {
      "question": "What is the capital of Finland?",
      "answers": [
        {
          "type": "LiteralAnswer",
          "answers": [
            "Helsinki"
          ],
          "placeholder": "The name in Finnish and English",
          "case_sensitive": false
        },
        {
          "type": "LiteralAnswer",
          "answers": [
            "Helsingfors"
          ],
          "placeholder": "The name in Swedish",
          "case_sensitive": false
        }
      ],
      "can_be_asked": true
    },
    {
      "question": "Did I spend too much time making this mod?",
      "answers": [
        {
          "type": "LiteralAnswer",
          "answers": [
            "Absolutely",
            "Yes"
          ],
          "placeholder": "",
          "case_sensitive": false
        }
      ],
      "can_be_asked": true
    },
    {
      "question": "What determines if your answer to this question is correct?",
      "answers": [
        {
          "type": "RegexAnswer",
          "answers": [
            "a? (regex|regular expression)"
          ],
          "placeholder": "",
          "simple_answer": "a regex or a regular expression"
        }
      ],
      "can_be_asked": true
    }
  ],
  "enabled": true,
  "max_ticks_until_reminder": 20000,
  "min_ticks_until_reminder": 1200,
  "max_answer_input_length": 256
}
```

## Screenshots of example config
![Helsinki](https://i.imgur.com/XEAdA7I.png)
![Time](https://i.imgur.com/oneFzPA.png)
![Regex](https://i.imgur.com/aRta3qp.png)
