
package edu.batstateu.javajolt.data;

import edu.batstateu.javajolt.Question;
import edu.batstateu.javajolt.enums.Difficulty;
import edu.batstateu.javajolt.enums.ChallengeTier;
import edu.batstateu.javajolt.enums.Topic;
import edu.batstateu.javajolt.types.IdentificationQuestion;
import edu.batstateu.javajolt.types.CodeQuestion;
import edu.batstateu.javajolt.types.MultipleChoiceQuestion;
import java.util.ArrayList;
import java.util.List;

public class ArraysQuestions {

    protected static final List<Question> getQuestions() {
        List<Question> questions = new ArrayList<>();

        // BEGINNER
        questions.add(new MultipleChoiceQuestion(
                501,
                "Which symbol is used to access an array element by index?",
                Topic.ARRAYS,
                Difficulty.BEGINNER,
                "Square brackets [] are used to access an array element using its index.",
                List.of("A. ()", "B. {}", "C. []", "D. <>"),
                "C"
        ));

        questions.add(new MultipleChoiceQuestion(
                502,
                "What is the index of the first element in a Java array?",
                Topic.ARRAYS,
                Difficulty.BEGINNER,
                "Java arrays use zero-based indexing, so the first element is at index 0.",
                List.of("A. 0", "B. 1", "C. -1", "D. 2"),
                "A"
        ));

        questions.add(new MultipleChoiceQuestion(
                503,
                "What does this code print?\n    int[] numbers = {10, 20, 30};\n    System.out.println(numbers[1]);",
                Topic.ARRAYS,
                Difficulty.BEGINNER,
                "The element at index 1 is 20 because array indexing starts at 0.",
                List.of("A. 10", "B. 20", "C. 30", "D. 1"),
                "B"
        ).withChallengeTier(ChallengeTier.APPLICATION));

        // FOUNDATIONAL — 508
        questions.add(new MultipleChoiceQuestion(
                508,
                "Which statement correctly declares an int array named ages containing 12, 15, and 18?",
                Topic.ARRAYS, Difficulty.BEGINNER,
                "An int array can be initialized using int[] and curly braces.",
                List.of("A. int ages = {12, 15, 18};", "B. int[] ages = {12, 15, 18};", "C. int[] ages = (12, 15, 18);", "D. int ages[] = 12, 15, 18;"),
                "B"
        ).withChallengeTier(ChallengeTier.FOUNDATIONAL));

        // FOUNDATIONAL — 509
        questions.add(new MultipleChoiceQuestion(
                509,
                "Which expression gives the number of elements in an array named numbers?",
                Topic.ARRAYS, Difficulty.BEGINNER,
                "The length field gives the size of a Java array, without parentheses.",
                List.of("A. numbers.length", "B. numbers.length()", "C. numbers.size()", "D. numbers.count"),
                "A"
        ).withChallengeTier(ChallengeTier.FOUNDATIONAL));

        // APPLICATION — 510
        questions.add(new MultipleChoiceQuestion(
                510,
                "What will this code print?\nint[] scores = {4, 7, 9, 12};\nSystem.out.println(scores.length);",
                Topic.ARRAYS, Difficulty.BEGINNER,
                "There are four values in the array.",
                List.of("A. 3", "B. 4", "C. 12", "D. 32"),
                "B"
        ).withChallengeTier(ChallengeTier.APPLICATION));

        // APPLICATION — 511
        questions.add(new MultipleChoiceQuestion(
                511,
                "What will this code print?\nint[] values = {6, 8, 10};\nvalues[1] = 3;\nSystem.out.println(values[0] + values[1]);",
                Topic.ARRAYS, Difficulty.BEGINNER,
                "Index 1 changes to 3; the printed sum is 6 + 3 = 9.",
                List.of("A. 9", "B. 14", "C. 13", "D. 11"),
                "A"
        ).withChallengeTier(ChallengeTier.APPLICATION));

        // APPLICATION — 512
        questions.add(new IdentificationQuestion(
                512,
                "What number will this code print?\nint[] values = {2, 5, 8};\nSystem.out.println(values[0] + values[2]);",
                Topic.ARRAYS, Difficulty.BEGINNER,
                "The first and third entries are 2 and 8, whose sum is 10.",
                List.of("10")
        ).withChallengeTier(ChallengeTier.APPLICATION));

        // CHALLENGE — 513
        questions.add(new MultipleChoiceQuestion(
                513,
                "What will this code print?\nint[] nums = {1, 3, 5, 7};\nint total = 0;\nfor (int j = 0; j < nums.length; j++) {\n    total += nums[j];\n}\nSystem.out.println(total);",
                Topic.ARRAYS, Difficulty.BEGINNER,
                "The loop sums 1 + 3 + 5 + 7 = 16.",
                List.of("A. 4", "B. 7", "C. 15", "D. 16"),
                "D"
        ).withChallengeTier(ChallengeTier.CHALLENGE));

        // CHALLENGE — 514
        questions.add(new MultipleChoiceQuestion(
                514,
                "What happens when these statements run?\nint[] data = {4, 9, 13};\nSystem.out.println(data[3]);",
                Topic.ARRAYS, Difficulty.BEGINNER,
                "The valid indices are 0, 1, and 2. Index 3 throws ArrayIndexOutOfBoundsException.",
                List.of("A. It prints 13", "B. It prints 0", "C. It throws ArrayIndexOutOfBoundsException", "D. It fails to compile"),
                "C"
        ).withChallengeTier(ChallengeTier.CHALLENGE));

        // CHALLENGE — 515
        questions.add(new IdentificationQuestion(
                515,
                "What number will this code print?\nint[] items = {3, 6, 9, 12};\nint total = 0;\nfor (int j = 1; j < items.length; j += 2) {\n    total += items[j];\n}\nSystem.out.println(total);",
                Topic.ARRAYS, Difficulty.BEGINNER,
                "The visited indices are 1 and 3: 6 + 12 = 18.",
                List.of("18")
        ).withChallengeTier(ChallengeTier.CHALLENGE));

        // CHALLENGE — 516
        questions.add(new CodeQuestion(
                516,
                "Write a Java program declaring an int array with values 2, 4, and 6. Use a for loop and the array .length field to sum its elements, then print the total.",
                Topic.ARRAYS, Difficulty.BEGINNER,
                "Example: int[] values={2,4,6}; int sum=0; for(int j=0;j<values.length;j++){sum+=values[j];} System.out.println(sum);",
                "12",
                List.of("int[]", "for", ".length"),
                List.of()
        ).withChallengeTier(ChallengeTier.CHALLENGE));

        // FOUNDATIONAL — 517
        questions.add(new MultipleChoiceQuestion(
                517,
                "For a nonempty int[] numbers, which expression reads its last element?",
                Topic.ARRAYS, Difficulty.BEGINNER,
                "The last index is the array length minus one.",
                List.of("A. numbers[numbers.length]", "B. numbers[numbers.length - 1]", "C. numbers[-1]", "D. numbers.length"),
                "B"
        ).withChallengeTier(ChallengeTier.FOUNDATIONAL));

        // FOUNDATIONAL — 518
        questions.add(new MultipleChoiceQuestion(
                518,
                "What is the default value of each element in new int[3]?",
                Topic.ARRAYS, Difficulty.BEGINNER,
                "New int array elements start at 0.",
                List.of("A. null", "B. -1", "C. 0", "D. 3"),
                "C"
        ).withChallengeTier(ChallengeTier.FOUNDATIONAL));

        // FOUNDATIONAL — 519
        questions.add(new MultipleChoiceQuestion(
                519,
                "Which declaration creates a String array with four elements?",
                Topic.ARRAYS, Difficulty.BEGINNER,
                "new String[4] makes an array with four String slots.",
                List.of("A. String[] names = new String[4];", "B. String names = new String[4];", "C. String[4] names = new String[];", "D. String[] names = new String(4);"),
                "A"
        ).withChallengeTier(ChallengeTier.FOUNDATIONAL));

        // FOUNDATIONAL — 520
        questions.add(new MultipleChoiceQuestion(
                520,
                "Which Java enhanced for-loop header visits each int in an int[] scores?",
                Topic.ARRAYS, Difficulty.BEGINNER,
                "Use a variable declaration, a colon, and the array reference.",
                List.of("A. for (int score = scores)", "B. for (score in scores)", "C. for (int score; scores)", "D. for (int score : scores)"),
                "D"
        ).withChallengeTier(ChallengeTier.FOUNDATIONAL));

        // APPLICATION — 521
        questions.add(new MultipleChoiceQuestion(
                521,
                "What will this code print?\nint[] nums = {5, 10, 15};\nnums[0] = nums[1];\nnums[2] += nums[0];\nSystem.out.println(nums[2]);",
                Topic.ARRAYS, Difficulty.BEGINNER,
                "The third element becomes 15 + 10 = 25.",
                List.of("A. 15", "B. 20", "C. 25", "D. 30"),
                "C"
        ).withChallengeTier(ChallengeTier.APPLICATION));

        // APPLICATION — 522
        questions.add(new IdentificationQuestion(
                522,
                "What number will this code print?\nint[] values = {3, 5, 7, 9};\nint total = 0;\nfor (int value : values) { total += value; }\nSystem.out.println(total);",
                Topic.ARRAYS, Difficulty.BEGINNER,
                "3 + 5 + 7 + 9 is 24.",
                List.of("24")
        ).withChallengeTier(ChallengeTier.APPLICATION));

        // APPLICATION — 523
        questions.add(new MultipleChoiceQuestion(
                523,
                "What will this code print?\nint[] values = {4, 7, 4, 9};\nint matches = 0;\nfor (int value : values) {\n    if (value == 4) matches++;\n}\nSystem.out.println(matches);",
                Topic.ARRAYS, Difficulty.BEGINNER,
                "The number 4 appears twice.",
                List.of("A. 1", "B. 2", "C. 3", "D. 4"),
                "B"
        ).withChallengeTier(ChallengeTier.APPLICATION));

        // APPLICATION — 524
        questions.add(new MultipleChoiceQuestion(
                524,
                "What will this code print?\nint[] ids = {9, 5, 7, 5};\nint position = -1;\nfor (int j = 0; j < ids.length; j++) {\n    if (ids[j] == 5) { position = j; break; }\n}\nSystem.out.println(position);",
                Topic.ARRAYS, Difficulty.BEGINNER,
                "The first matching value is at index 1, where the search stops.",
                List.of("A. 0", "B. 1", "C. 3", "D. -1"),
                "B"
        ).withChallengeTier(ChallengeTier.APPLICATION));

        // CHALLENGE — 525
        questions.add(new MultipleChoiceQuestion(
                525,
                "What will this code print?\nint[] values = {3, 11, 8, 5};\nint max = values[0];\nfor (int value : values) {\n    if (value > max) max = value;\n}\nSystem.out.println(max);",
                Topic.ARRAYS, Difficulty.BEGINNER,
                "The maximum value is 11.",
                List.of("A. 3", "B. 5", "C. 8", "D. 11"),
                "D"
        ).withChallengeTier(ChallengeTier.CHALLENGE));

        // CHALLENGE — 526
        questions.add(new IdentificationQuestion(
                526,
                "What number will this code print?\nint[] values = {8, 3, 9, 2};\nint min = values[0];\nfor (int j = 1; j < values.length; j++) {\n    if (values[j] < min) min = values[j];\n}\nSystem.out.println(min);",
                Topic.ARRAYS, Difficulty.BEGINNER,
                "The minimum in the array is 2.",
                List.of("2")
        ).withChallengeTier(ChallengeTier.CHALLENGE));

        // CHALLENGE — 527
        questions.add(new MultipleChoiceQuestion(
                527,
                "What will this code print?\nint[] values = {2, 4, 6, 8};\nfor (int j = 0; j < values.length; j++) {\n    if (j % 2 == 0) values[j] += 1;\n}\nSystem.out.println(values[0] + \" \" + values[1] + \" \" + values[2] + \" \" + values[3]);",
                Topic.ARRAYS, Difficulty.BEGINNER,
                "Only entries at even indices 0 and 2 increase by one.",
                List.of("A. 2 5 6 9", "B. 3 4 7 8", "C. 3 5 7 9", "D. 2 4 6 8"),
                "B"
        ).withChallengeTier(ChallengeTier.CHALLENGE));

        // CHALLENGE — 528
        questions.add(new CodeQuestion(
                528,
                "Write a Java program declaring int[] values = {3, 11, 7, 9}. Use a for loop, .length, and an if statement to find the greatest element and print only that number.",
                Topic.ARRAYS, Difficulty.BEGINNER,
                "Start with int max = values[0] and update it when a larger value is found.",
                "11",
                List.of("int[]", "for", ".length", "if"),
                List.of()
        ).withChallengeTier(ChallengeTier.CHALLENGE));

        // INTERMEDIATE
        questions.add(new MultipleChoiceQuestion(
                504,
                "Which property gives the number of elements in an array named values?",
                Topic.ARRAYS,
                Difficulty.INTERMEDIATE,
                "The length field stores the number of elements in an array.",
                List.of("A. values.size()", "B. values.length", "C. values.count()", "D. values.capacity"),
                "B"
        ));

        questions.add(new IdentificationQuestion(
                505,
                "What exception occurs when code accesses an invalid array index? (one class name)",
                Topic.ARRAYS,
                Difficulty.INTERMEDIATE,
                "Accessing outside the valid range throws ArrayIndexOutOfBoundsException.",
                List.of("ArrayIndexOutOfBoundsException")
        ));

        // ADVANCED
        questions.add(new MultipleChoiceQuestion(
                506,
                "What is printed?\n    int[] values = {2, 4, 6};\n    int total = 0;\n    for (int value : values) total += value;\n    System.out.println(total);",
                Topic.ARRAYS,
                Difficulty.ADVANCED,
                "The enhanced for loop adds every element: 2 + 4 + 6 equals 12.",
                List.of("A. 6", "B. 10", "C. 12", "D. 246"),
                "C"
        ));

        questions.add(new MultipleChoiceQuestion(
                507,
                "Which declaration creates a two-dimensional integer array with 2 rows and 3 columns?",
                Topic.ARRAYS,
                Difficulty.ADVANCED,
                "The syntax int[2][3] creates an array with two rows, each sized for three integers.",
                List.of("A. int[2, 3] table", "B. int[][] table = new int[2][3]", "C. int table = new int(2, 3)", "D. array<int> table[2][3]"),
                "B"
        ));

        return questions;
    }
}
