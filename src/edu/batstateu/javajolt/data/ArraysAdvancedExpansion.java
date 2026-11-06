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

/** Additional Arrays advanced questions. Does not replace existing banks. */
public final class ArraysAdvancedExpansion {
    private ArraysAdvancedExpansion() {}

    public static List<Question> getQuestions() {
        List<Question> questions = new ArrayList<>();

        // FOUNDATIONAL
        questions.add(new MultipleChoiceQuestion(
                552,
                "Which statement is true about int[][] jagged arrays?",
                Topic.ARRAYS, Difficulty.ADVANCED,
                "In Java, a 2D array is an array of arrays, so row lengths may differ.",
                List.of(
                        "A. Rows can have different lengths",
                        "B. Every row must have equal length",
                        "C. Each row must be non-null",
                        "D. They cannot be iterated"
                ), "A"
        ).withChallengeTier(ChallengeTier.FOUNDATIONAL));

        questions.add(new MultipleChoiceQuestion(
                553,
                "What does Arrays.sort(int[]) do?",
                Topic.ARRAYS, Difficulty.ADVANCED,
                "Arrays.sort mutates the input int array to ascending order.",
                List.of(
                        "A. Removes duplicate values",
                        "B. Sorts the array in place in ascending order",
                        "C. Returns an unrelated sorted copy only",
                        "D. Always sorts descending"
                ), "B"
        ).withChallengeTier(ChallengeTier.FOUNDATIONAL));

        questions.add(new MultipleChoiceQuestion(
                554,
                "What does Arrays.binarySearch require for standard meaningful results?",
                Topic.ARRAYS, Difficulty.ADVANCED,
                "Binary search assumes the input array is sorted in the appropriate ordering.",
                List.of(
                        "A. Input containing no repeated values",
                        "B. Input with even length",
                        "C. Input sorted in ascending order",
                        "D. Input sorted in descending order"
                ), "C"
        ).withChallengeTier(ChallengeTier.FOUNDATIONAL));

        questions.add(new MultipleChoiceQuestion(
                555,
                "Which statement about System.arraycopy is correct?",
                Topic.ARRAYS, Difficulty.ADVANCED,
                "System.arraycopy performs bulk array copying.",
                List.of(
                        "A. It always resizes the target",
                        "B. It only copies Strings",
                        "C. It sorts while copying",
                        "D. It can copy a range of elements between arrays"
                ), "D"
        ).withChallengeTier(ChallengeTier.FOUNDATIONAL));

        questions.add(new MultipleChoiceQuestion(
                556,
                "What does Arrays.equals(a,b) compare for primitive int arrays?",
                Topic.ARRAYS, Difficulty.ADVANCED,
                "Arrays.equals tests corresponding elements and lengths.",
                List.of(
                        "A. Element values in order",
                        "B. Only array reference identity",
                        "C. Only lengths",
                        "D. Only the first element"
                ), "A"
        ).withChallengeTier(ChallengeTier.FOUNDATIONAL));

        questions.add(new MultipleChoiceQuestion(
                557,
                "What happens when you call a.length on a null int[] reference a?",
                Topic.ARRAYS, Difficulty.ADVANCED,
                "Dereferencing null to read length throws NullPointerException.",
                List.of(
                        "A. An empty array is allocated",
                        "B. NullPointerException",
                        "C. 0",
                        "D. ArrayIndexOutOfBoundsException"
                ), "B"
        ).withChallengeTier(ChallengeTier.FOUNDATIONAL));

        questions.add(new MultipleChoiceQuestion(
                558,
                "What is the default value of a new String[2] element?",
                Topic.ARRAYS, Difficulty.ADVANCED,
                "Reference-type array elements default to null.",
                List.of(
                        "A. \"null\"",
                        "B. A blank space",
                        "C. null",
                        "D. \"\""
                ), "C"
        ).withChallengeTier(ChallengeTier.FOUNDATIONAL));

        // APPLICATION
        questions.add(new MultipleChoiceQuestion(
                559,
                "What prints?\nint[][] a = {{1},{2,3,4}};\nSystem.out.println(a[1].length);",
                Topic.ARRAYS, Difficulty.ADVANCED,
                "The second row has three elements.",
                List.of(
                        "A. 2",
                        "B. 1",
                        "C. 4",
                        "D. 3"
                ), "D"
        ).withChallengeTier(ChallengeTier.APPLICATION));

        questions.add(new MultipleChoiceQuestion(
                560,
                "What prints?\nint[] a = {4,1,3};\njava.util.Arrays.sort(a);\nSystem.out.println(a[0]);",
                Topic.ARRAYS, Difficulty.ADVANCED,
                "Sorting ascending places one first.",
                List.of(
                        "A. 1",
                        "B. 4",
                        "C. 3",
                        "D. 0"
                ), "A"
        ).withChallengeTier(ChallengeTier.APPLICATION));

        questions.add(new IdentificationQuestion(
                561,
                "What prints?\nint[] a = {1,2,3,4};\nint[] b = java.util.Arrays.copyOfRange(a,1,3);\nSystem.out.println(b.length);",
                Topic.ARRAYS, Difficulty.ADVANCED,
                "The copy includes indexes 1 and 2 but excludes end index 3.",
                List.of("2")
        ).withChallengeTier(ChallengeTier.APPLICATION));

        questions.add(new MultipleChoiceQuestion(
                562,
                "What prints?\nint[][] a = new int[2][];\na[0] = new int[3];\na[1] = new int[1];\nSystem.out.println(a[0].length + a[1].length);",
                Topic.ARRAYS, Difficulty.ADVANCED,
                "The row lengths are three and one.",
                List.of(
                        "A. 2",
                        "B. 5",
                        "C. 4",
                        "D. 3"
                ), "C"
        ).withChallengeTier(ChallengeTier.APPLICATION));

        questions.add(new MultipleChoiceQuestion(
                563,
                "What prints?\nint[] a = {3,6,9};\nint[] b = a.clone();\nSystem.out.println(java.util.Arrays.equals(a,b));",
                Topic.ARRAYS, Difficulty.ADVANCED,
                "The cloned array has equal primitive contents.",
                List.of(
                        "A. false",
                        "B. null",
                        "C. Compilation error",
                        "D. true"
                ), "D"
        ).withChallengeTier(ChallengeTier.APPLICATION));

        questions.add(new MultipleChoiceQuestion(
                564,
                "What prints?\nint[] a = {2,4,6,8};\nint idx = java.util.Arrays.binarySearch(a,6);\nSystem.out.println(idx);",
                Topic.ARRAYS, Difficulty.ADVANCED,
                "Six is at index two in the sorted array.",
                List.of(
                        "A. 2",
                        "B. 3",
                        "C. 1",
                        "D. -1"
                ), "A"
        ).withChallengeTier(ChallengeTier.APPLICATION));

        questions.add(new IdentificationQuestion(
                565,
                "What prints?\nint[][] matrix = {{1,2},{3,4},{5,6}};\nSystem.out.println(matrix.length);",
                Topic.ARRAYS, Difficulty.ADVANCED,
                "The outer array has three row arrays.",
                List.of("3")
        ).withChallengeTier(ChallengeTier.APPLICATION));

        // CHALLENGE
        questions.add(new MultipleChoiceQuestion(
                566,
                "What prints?\nint[][] a = {{1,2},{3,4}};\nint sum=0;\nfor(int i=0;i<a.length;i++) sum+=a[i][i];\nSystem.out.println(sum);",
                Topic.ARRAYS, Difficulty.ADVANCED,
                "The diagonal entries are one and four.",
                List.of(
                        "A. 3",
                        "B. 7",
                        "C. 5",
                        "D. 10"
                ), "C"
        ).withChallengeTier(ChallengeTier.CHALLENGE));

        questions.add(new MultipleChoiceQuestion(
                567,
                "What prints?\nint[] a = {1,2,3};\nint[] b = a;\nb[1] = 9;\nSystem.out.println(java.util.Arrays.equals(a,b));",
                Topic.ARRAYS, Difficulty.ADVANCED,
                "Both variables refer to the same array so contents are identical.",
                List.of(
                        "A. false",
                        "B. Compilation error",
                        "C. null",
                        "D. true"
                ), "D"
        ).withChallengeTier(ChallengeTier.CHALLENGE));

        questions.add(new IdentificationQuestion(
                568,
                "What prints?\nint[][] a = {{1,2,3},{4},{5,6}};\nint sum = 0;\nfor(int[] row:a) sum += row.length;\nSystem.out.println(sum);",
                Topic.ARRAYS, Difficulty.ADVANCED,
                "Row lengths are 3, 1, and 2, totaling six.",
                List.of("6")
        ).withChallengeTier(ChallengeTier.CHALLENGE));

        questions.add(new MultipleChoiceQuestion(
                569,
                "What prints?\nint[] a = {2,4,6,8};\nSystem.arraycopy(a,1,a,0,3);\nSystem.out.println(a[0] + \":\" + a[2]);",
                Topic.ARRAYS, Difficulty.ADVANCED,
                "The overlapping copy moves values 4,6,8 to positions 0,1,2.",
                List.of(
                        "A. 6:8",
                        "B. 4:8",
                        "C. 2:6",
                        "D. 4:6"
                ), "B"
        ).withChallengeTier(ChallengeTier.CHALLENGE));

        questions.add(new MultipleChoiceQuestion(
                570,
                "What prints?\nint[][] a = new int[2][2];\na[0][1]=7;\nSystem.out.println(a[1][0]);",
                Topic.ARRAYS, Difficulty.ADVANCED,
                "Unassigned elements in a newly allocated int matrix default to zero.",
                List.of(
                        "A. null",
                        "B. Compilation error",
                        "C. 0",
                        "D. 7"
                ), "C"
        ).withChallengeTier(ChallengeTier.CHALLENGE));

        questions.add(new IdentificationQuestion(
                571,
                "What prints?\nint[] a = {8,3,9,1};\nint min=a[0];\nfor(int i=1;i<a.length;i++) if(a[i]<min) min=a[i];\nSystem.out.println(min);",
                Topic.ARRAYS, Difficulty.ADVANCED,
                "The smallest value is one.",
                List.of("1")
        ).withChallengeTier(ChallengeTier.CHALLENGE));

        questions.add(new CodeQuestion(
                572,
                "In the supplied Code class, declare int[][] grid = {{1,2},{3,4}}; use nested for loops to print the sum of all elements.",
                Topic.ARRAYS, Difficulty.ADVANCED,
                "The four elements total ten.",
                "10",
                List.of("int[][]", "for", "System.out.println"),
                List.of()
        ).withChallengeTier(ChallengeTier.CHALLENGE));

        questions.add(new CodeQuestion(
                573,
                "In the supplied Code class, declare int[] a = {7,2,9,4}; use Arrays.sort to sort ascending, then print the smallest value.",
                Topic.ARRAYS, Difficulty.ADVANCED,
                "Sorting ascending moves two to index zero.",
                "2",
                List.of("Arrays.sort", "int[]", "System.out.println"),
                List.of()
        ).withChallengeTier(ChallengeTier.CHALLENGE));

        return questions;
    }
}
