package edu.batstateu.javajolt.data;

import edu.batstateu.javajolt.Question;
import edu.batstateu.javajolt.enums.ChallengeTier;
import edu.batstateu.javajolt.enums.Difficulty;
import edu.batstateu.javajolt.enums.Topic;
import edu.batstateu.javajolt.types.CodeQuestion;
import edu.batstateu.javajolt.types.IdentificationQuestion;
import edu.batstateu.javajolt.types.MultipleChoiceQuestion;
import java.util.ArrayList;
import java.util.List;

/** Additional Arrays intermediate questions. Does not replace existing banks. */
public final class ArraysIntermediateExpansion {
    private ArraysIntermediateExpansion() {}

    public static List<Question> getQuestions() {
        List<Question> questions = new ArrayList<>();

        // FOUNDATIONAL
        questions.add(new MultipleChoiceQuestion(
                530,
                "What is the index of the first element in a Java array?",
                Topic.ARRAYS, Difficulty.INTERMEDIATE,
                "Java arrays are zero-indexed.",
                List.of(
                        "A. -1",
                        "B. array.length",
                        "C. 0",
                        "D. 1"
                ), "C"
        ).withChallengeTier(ChallengeTier.FOUNDATIONAL));

        questions.add(new MultipleChoiceQuestion(
                531,
                "What is the last valid index in a nonempty array of length n?",
                Topic.ARRAYS, Difficulty.INTERMEDIATE,
                "Indices range from zero to one less than the array length.",
                List.of(
                        "A. n",
                        "B. 1",
                        "C. n + 1",
                        "D. n - 1"
                ), "D"
        ).withChallengeTier(ChallengeTier.FOUNDATIONAL));

        questions.add(new MultipleChoiceQuestion(
                532,
                "Which declaration creates a 2D array of 3 rows and 2 columns?",
                Topic.ARRAYS, Difficulty.INTERMEDIATE,
                "A two-dimensional int array uses two sets of brackets.",
                List.of(
                        "A. int[][] grid = new int[3][2];",
                        "B. int[] grid = new int[3][2];",
                        "C. int grid = new int[3][2];",
                        "D. int[][] grid = new int(3,2);"
                ), "A"
        ).withChallengeTier(ChallengeTier.FOUNDATIONAL));

        questions.add(new MultipleChoiceQuestion(
                533,
                "What is the default value of elements in a new boolean[4]?",
                Topic.ARRAYS, Difficulty.INTERMEDIATE,
                "Primitive boolean array elements default to false.",
                List.of(
                        "A. 0",
                        "B. false",
                        "C. true",
                        "D. null"
                ), "B"
        ).withChallengeTier(ChallengeTier.FOUNDATIONAL));

        questions.add(new MultipleChoiceQuestion(
                534,
                "Which statement creates an independent copy of a primitive int array?",
                Topic.ARRAYS, Difficulty.INTERMEDIATE,
                "clone constructs a distinct array object with copied primitive elements.",
                List.of(
                        "A. int[] b = (int[]) a;",
                        "B. int[] b = a.length;",
                        "C. int[] b = a.clone();",
                        "D. int[] b = a;"
                ), "C"
        ).withChallengeTier(ChallengeTier.FOUNDATIONAL));

        questions.add(new MultipleChoiceQuestion(
                535,
                "What does arr.length return for a Java array?",
                Topic.ARRAYS, Difficulty.INTERMEDIATE,
                "length is a final field recording element count.",
                List.of(
                        "A. The last valid index",
                        "B. The number of nonzero values",
                        "C. The memory address",
                        "D. The number of its elements"
                ), "D"
        ).withChallengeTier(ChallengeTier.FOUNDATIONAL));

        questions.add(new MultipleChoiceQuestion(
                536,
                "What happens when an array is indexed with -1?",
                Topic.ARRAYS, Difficulty.INTERMEDIATE,
                "Negative array indices are invalid.",
                List.of(
                        "A. ArrayIndexOutOfBoundsException",
                        "B. A read of the last element",
                        "C. NullPointerException",
                        "D. Silent wraparound"
                ), "A"
        ).withChallengeTier(ChallengeTier.FOUNDATIONAL));

        // APPLICATION
        questions.add(new MultipleChoiceQuestion(
                537,
                "What prints?\nint[] a = {2,4,6};\nSystem.out.println(a[1] + a[2]);",
                Topic.ARRAYS, Difficulty.INTERMEDIATE,
                "Indices one and two contain four and six.",
                List.of(
                        "A. 12",
                        "B. 10",
                        "C. 6",
                        "D. 8"
                ), "B"
        ).withChallengeTier(ChallengeTier.APPLICATION));

        questions.add(new IdentificationQuestion(
                538,
                "What prints?\nint[] n = {1,3,5};\nint s = 0;\nfor (int v : n) s += v;\nSystem.out.println(s);",
                Topic.ARRAYS, Difficulty.INTERMEDIATE,
                "An enhanced for sums 1+3+5.",
                List.of("9")
        ).withChallengeTier(ChallengeTier.APPLICATION));

        questions.add(new MultipleChoiceQuestion(
                539,
                "What prints?\nint[] a = {4,5};\nint[] b = a;\nb[0] = 9;\nSystem.out.println(a[0]);",
                Topic.ARRAYS, Difficulty.INTERMEDIATE,
                "Both variables refer to the same array, so the update is visible through a.",
                List.of(
                        "A. 4",
                        "B. 5",
                        "C. Compilation error",
                        "D. 9"
                ), "D"
        ).withChallengeTier(ChallengeTier.APPLICATION));

        questions.add(new MultipleChoiceQuestion(
                540,
                "What prints?\nint[] a = {4,5};\nint[] b = a.clone();\nb[0] = 9;\nSystem.out.println(a[0]);",
                Topic.ARRAYS, Difficulty.INTERMEDIATE,
                "clone makes a separate array of primitive values.",
                List.of(
                        "A. 4",
                        "B. 9",
                        "C. 5",
                        "D. Compilation error"
                ), "A"
        ).withChallengeTier(ChallengeTier.APPLICATION));

        questions.add(new MultipleChoiceQuestion(
                541,
                "What prints?\nint[][] grid = {{1,2},{3,4}};\nSystem.out.println(grid[1][0]);",
                Topic.ARRAYS, Difficulty.INTERMEDIATE,
                "Row one is {3,4} and column zero is three.",
                List.of(
                        "A. 4",
                        "B. 3",
                        "C. 2",
                        "D. 1"
                ), "B"
        ).withChallengeTier(ChallengeTier.APPLICATION));

        questions.add(new IdentificationQuestion(
                542,
                "What prints?\nint[] a = {7,9,11,13};\nSystem.out.println(a.length);",
                Topic.ARRAYS, Difficulty.INTERMEDIATE,
                "The array contains four elements.",
                List.of("4")
        ).withChallengeTier(ChallengeTier.APPLICATION));

        questions.add(new MultipleChoiceQuestion(
                543,
                "What prints?\nint[] a = {4,8,12};\nfor (int i=0;i<a.length;i++) if(i==1) a[i]+=2;\nSystem.out.println(a[1]);",
                Topic.ARRAYS, Difficulty.INTERMEDIATE,
                "Only index one changes from 8 to 10.",
                List.of(
                        "A. 8",
                        "B. 12",
                        "C. 14",
                        "D. 10"
                ), "D"
        ).withChallengeTier(ChallengeTier.APPLICATION));

        // CHALLENGE
        questions.add(new MultipleChoiceQuestion(
                544,
                "What prints?\nint[] a = {1,2,3};\nfor (int i=0;i<a.length;i++) a[i]*=2;\nSystem.out.println(a[2]);",
                Topic.ARRAYS, Difficulty.INTERMEDIATE,
                "The third value is doubled from 3 to 6.",
                List.of(
                        "A. 6",
                        "B. 3",
                        "C. 4",
                        "D. 12"
                ), "A"
        ).withChallengeTier(ChallengeTier.CHALLENGE));

        questions.add(new MultipleChoiceQuestion(
                545,
                "What prints?\nint[] a = {1,2,3,4};\nint s=0;\nfor(int i=1;i<a.length;i+=2) s+=a[i];\nSystem.out.println(s);",
                Topic.ARRAYS, Difficulty.INTERMEDIATE,
                "Odd indices hold 2 and 4, totaling six.",
                List.of(
                        "A. 3",
                        "B. 6",
                        "C. 4",
                        "D. 10"
                ), "B"
        ).withChallengeTier(ChallengeTier.CHALLENGE));

        questions.add(new IdentificationQuestion(
                546,
                "What prints?\nint[][] a = {{1,2,3},{4,5,6}};\nint sum = 0;\nfor(int[] row:a) for(int value:row) sum += value;\nSystem.out.println(sum);",
                Topic.ARRAYS, Difficulty.INTERMEDIATE,
                "The two rows sum to 6 and 15, totaling 21.",
                List.of("21")
        ).withChallengeTier(ChallengeTier.CHALLENGE));

        questions.add(new MultipleChoiceQuestion(
                547,
                "What prints?\nint[] a = {5,2,8};\nint max = a[0];\nfor(int v:a) if(v>max) max=v;\nSystem.out.println(max);",
                Topic.ARRAYS, Difficulty.INTERMEDIATE,
                "The maximum element is eight.",
                List.of(
                        "A. 5",
                        "B. 2",
                        "C. 15",
                        "D. 8"
                ), "D"
        ).withChallengeTier(ChallengeTier.CHALLENGE));

        questions.add(new MultipleChoiceQuestion(
                548,
                "What prints?\nint[] a = {1,2,3};\nint[] b = a;\na = new int[]{7,8};\nSystem.out.println(b[0]);",
                Topic.ARRAYS, Difficulty.INTERMEDIATE,
                "Reassigning a changes its reference but b still refers to the old array.",
                List.of(
                        "A. 1",
                        "B. 7",
                        "C. 8",
                        "D. Compilation error"
                ), "A"
        ).withChallengeTier(ChallengeTier.CHALLENGE));

        questions.add(new IdentificationQuestion(
                549,
                "What prints?\nint[] a = {4,7,4,9,4};\nint c=0;\nfor(int v:a) if(v==4) c++;\nSystem.out.println(c);",
                Topic.ARRAYS, Difficulty.INTERMEDIATE,
                "The value four appears three times.",
                List.of("3")
        ).withChallengeTier(ChallengeTier.CHALLENGE));

        questions.add(new CodeQuestion(
                550,
                "In the supplied Code class, create int[] a = {3,4,5}; sum its values using an enhanced for loop and print the result.",
                Topic.ARRAYS, Difficulty.INTERMEDIATE,
                "Three plus four plus five equals twelve.",
                "12",
                List.of("int[]", "for", ":", "System.out.println"),
                List.of()
        ).withChallengeTier(ChallengeTier.CHALLENGE));

        questions.add(new CodeQuestion(
                551,
                "In the supplied Code class, create int[] scores = {5,11,7}; use a for loop and an if statement to find the maximum, then print it.",
                Topic.ARRAYS, Difficulty.INTERMEDIATE,
                "The maximum value is 11.",
                "11",
                List.of("int[]", "for", "if", "System.out.println"),
                List.of()
        ).withChallengeTier(ChallengeTier.CHALLENGE));

        return questions;
    }
}
