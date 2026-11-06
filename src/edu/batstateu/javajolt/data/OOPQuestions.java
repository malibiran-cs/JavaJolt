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

public class OOPQuestions {

    protected static final List<Question> getQuestions() {
        List<Question> questions = new ArrayList<>();

        // ==================================================
        // BEGINNER — FOUNDATIONAL (8 questions)
        // ==================================================

        questions.add(new MultipleChoiceQuestion(
                701,
                "Which keyword is used to create an object in Java?",
                Topic.OOP, Difficulty.BEGINNER,
                "The new keyword creates an object by calling a constructor.",
                List.of("A. class", "B. this", "C. new", "D. void"),
                "C"
        ).withChallengeTier(ChallengeTier.FOUNDATIONAL));

        questions.add(new MultipleChoiceQuestion(
                702,
                "What is a class in Java?",
                Topic.OOP, Difficulty.BEGINNER,
                "A class is a blueprint defining the fields and behavior of objects.",
                List.of("A. A blueprint for creating objects",
                        "B. A loop structure",
                        "C. A data type for decimals only",
                        "D. An exception handler"),
                "A"
        ).withChallengeTier(ChallengeTier.FOUNDATIONAL));

        questions.add(new MultipleChoiceQuestion(
                703,
                "Which OOP concept allows a class to inherit features from another class?",
                Topic.OOP, Difficulty.BEGINNER,
                "Inheritance allows a subclass to reuse accessible members of a superclass.",
                List.of("A. Encapsulation", "B. Inheritance",
                        "C. Compilation", "D. Iteration"),
                "B"
        ).withChallengeTier(ChallengeTier.FOUNDATIONAL));

        questions.add(new MultipleChoiceQuestion(
                708,
                "What is an object in Java?",
                Topic.OOP, Difficulty.BEGINNER,
                "An object is an instance of a class, with its own state and behavior.",
                List.of("A. A package import", "B. An instance of a class",
                        "C. A loop condition", "D. A compiler command"),
                "B"
        ).withChallengeTier(ChallengeTier.FOUNDATIONAL));

        questions.add(new MultipleChoiceQuestion(
                709,
                "Inside an instance method, what does the keyword this refer to?",
                Topic.OOP, Difficulty.BEGINNER,
                "this refers to the current object whose instance method is running.",
                List.of("A. The current object", "B. The parent class",
                        "C. Every object in memory", "D. The Java compiler"),
                "A"
        ).withChallengeTier(ChallengeTier.FOUNDATIONAL));

        questions.add(new MultipleChoiceQuestion(
                710,
                "What is the primary purpose of a constructor in Java?",
                Topic.OOP, Difficulty.BEGINNER,
                "A constructor initializes a new object when it is created.",
                List.of("A. To repeat a statement", "B. To import a library",
                        "C. To initialize an object", "D. To terminate the program"),
                "C"
        ).withChallengeTier(ChallengeTier.FOUNDATIONAL));

        questions.add(new MultipleChoiceQuestion(
                711,
                "Which access modifier limits a field to its declaring class?",
                Topic.OOP, Difficulty.BEGINNER,
                "private prevents direct access from code outside the declaring class.",
                List.of("A. public", "B. protected", "C. static", "D. private"),
                "D"
        ).withChallengeTier(ChallengeTier.FOUNDATIONAL));

        questions.add(new MultipleChoiceQuestion(
                712,
                "Which statement best describes a method in Java?",
                Topic.OOP, Difficulty.BEGINNER,
                "A method groups instructions that perform an action or return a value.",
                List.of("A. A named block of behavior",
                        "B. A file containing only numbers",
                        "C. An array index", "D. An object address"),
                "A"
        ).withChallengeTier(ChallengeTier.FOUNDATIONAL));

        // ==================================================
        // BEGINNER — APPLICATION (8 questions)
        // ==================================================

        questions.add(new MultipleChoiceQuestion(
                713,
                "What will this code print?\n"
                + "class Box { int size = 4; }\n"
                + "Box box = new Box();\n"
                + "System.out.println(box.size);",
                Topic.OOP, Difficulty.BEGINNER,
                "The new Box object starts with size equal to 4.",
                List.of("A. 0", "B. 4", "C. size", "D. Compilation error"),
                "B"
        ).withChallengeTier(ChallengeTier.APPLICATION));

        questions.add(new MultipleChoiceQuestion(
                714,
                "What will this code print?\n"
                + "class Counter {\n"
                + "    int count;\n"
                + "    Counter(int start) { count = start; }\n"
                + "}\n"
                + "Counter counter = new Counter(6);\n"
                + "System.out.println(counter.count);",
                Topic.OOP, Difficulty.BEGINNER,
                "The constructor stores the argument 6 in the count field.",
                List.of("A. 0", "B. 1", "C. 6", "D. Compilation error"),
                "C"
        ).withChallengeTier(ChallengeTier.APPLICATION));

        questions.add(new MultipleChoiceQuestion(
                715,
                "What will this code print?\n"
                + "class Calculator {\n"
                + "    int twice(int n) { return n * 2; }\n"
                + "}\n"
                + "Calculator calc = new Calculator();\n"
                + "System.out.println(calc.twice(4));",
                Topic.OOP, Difficulty.BEGINNER,
                "The method returns 4 multiplied by 2, which is 8.",
                List.of("A. 2", "B. 4", "C. 6", "D. 8"),
                "D"
        ).withChallengeTier(ChallengeTier.APPLICATION));

        questions.add(new MultipleChoiceQuestion(
                716,
                "What will this code print?\n"
                + "class Player { int hp = 10; }\n"
                + "Player first = new Player();\n"
                + "Player second = new Player();\n"
                + "first.hp = 3;\n"
                + "System.out.println(second.hp);",
                Topic.OOP, Difficulty.BEGINNER,
                "first and second refer to different objects, so second.hp remains 10.",
                List.of("A. 3", "B. 7", "C. 10", "D. 13"),
                "C"
        ).withChallengeTier(ChallengeTier.APPLICATION));

        questions.add(new MultipleChoiceQuestion(
                717,
                "A separate class contains: class Person { private int age = 20; }. "
                + "Which expression can read age from another class without changing Person?",
                Topic.OOP, Difficulty.BEGINNER,
                "A private field cannot be read directly from another class. "
                + "Person would need an accessible method such as a getter.",
                List.of("A. person.age", "B. Person.age",
                        "C. person.private(age)", "D. None of these expressions"),
                "D"
        ).withChallengeTier(ChallengeTier.APPLICATION));

        questions.add(new IdentificationQuestion(
                718,
                "What number will this code print?\n"
                + "class Score {\n"
                + "    int value = 3;\n"
                + "    void update(int value) { this.value = value; }\n"
                + "}\n"
                + "Score score = new Score();\n"
                + "score.update(9);\n"
                + "System.out.println(score.value);",
                Topic.OOP, Difficulty.BEGINNER,
                "this.value refers to the field, which is assigned the argument 9.",
                List.of("9")
        ).withChallengeTier(ChallengeTier.APPLICATION));

        questions.add(new MultipleChoiceQuestion(
                719,
                "What will this code print?\n"
                + "class Animal { String type = \"Animal\"; }\n"
                + "class Cat extends Animal { }\n"
                + "Cat pet = new Cat();\n"
                + "System.out.println(pet.type);",
                Topic.OOP, Difficulty.BEGINNER,
                "Cat inherits the accessible type field from Animal.",
                List.of("A. Animal", "B. Cat", "C. null", "D. Compilation error"),
                "A"
        ).withChallengeTier(ChallengeTier.APPLICATION));

        questions.add(new CodeQuestion(
                720,
                "Inside the provided Code class, write a program that declares "
                + "a local class Player with an int hp field initialized to 10 "
                + "and a void hit() method that subtracts 2 from hp. "
                + "Create a Player, call hit(), and print the remaining HP.",
                Topic.OOP, Difficulty.BEGINNER,
                "Example inside main: class Player { int hp = 10; "
                + "void hit() { hp -= 2; } } Player p = new Player(); "
                + "p.hit(); System.out.println(p.hp);",
                "8",
                List.of("class Player", "new Player", "void hit", "System.out.println"),
                List.of()
        ).withChallengeTier(ChallengeTier.APPLICATION));

        // ==================================================
        // BEGINNER — CHALLENGE (8 questions)
        // ==================================================

        questions.add(new MultipleChoiceQuestion(
                721,
                "What will this code print?\n"
                + "class Printer {\n"
                + "    int show(int n) { return n + 1; }\n"
                + "    int show(int a, int b) { return a + b; }\n"
                + "}\n"
                + "Printer p = new Printer();\n"
                + "System.out.println(p.show(3) + p.show(2, 4));",
                Topic.OOP, Difficulty.BEGINNER,
                "Overloading chooses show(int) and show(int,int). "
                + "Their return values are 4 and 6, totaling 10.",
                List.of("A. 6", "B. 7", "C. 10", "D. Compilation error"),
                "C"
        ).withChallengeTier(ChallengeTier.CHALLENGE));

        questions.add(new MultipleChoiceQuestion(
                722,
                "What will this code print?\n"
                + "class Animal { String sound() { return \"Animal\"; } }\n"
                + "class Dog extends Animal {\n"
                + "    @Override String sound() { return \"Woof\"; }\n"
                + "}\n"
                + "Animal pet = new Dog();\n"
                + "System.out.println(pet.sound());",
                Topic.OOP, Difficulty.BEGINNER,
                "Overriding uses the Dog version of sound() at runtime.",
                List.of("A. Animal", "B. Woof", "C. Dog", "D. Compilation error"),
                "B"
        ).withChallengeTier(ChallengeTier.CHALLENGE));

        questions.add(new MultipleChoiceQuestion(
                723,
                "What will this code print?\n"
                + "class Ticket {\n"
                + "    static int sold = 0;\n"
                + "    Ticket() { sold++; }\n"
                + "}\n"
                + "new Ticket();\n"
                + "new Ticket();\n"
                + "System.out.println(Ticket.sold);",
                Topic.OOP, Difficulty.BEGINNER,
                "The static field sold is shared and increments for both new objects.",
                List.of("A. 0", "B. 1", "C. 2", "D. Compilation error"),
                "C"
        ).withChallengeTier(ChallengeTier.CHALLENGE));

        questions.add(new MultipleChoiceQuestion(
                724,
                "What will this code print?\n"
                + "class Book {\n"
                + "    int pages;\n"
                + "    Book() { pages = 10; }\n"
                + "    Book(int pages) { this.pages = pages; }\n"
                + "}\n"
                + "Book first = new Book();\n"
                + "Book second = new Book(25);\n"
                + "System.out.println(first.pages + second.pages);",
                Topic.OOP, Difficulty.BEGINNER,
                "Different constructors initialize the objects to 10 and 25 pages.",
                List.of("A. 10", "B. 25", "C. 30", "D. 35"),
                "D"
        ).withChallengeTier(ChallengeTier.CHALLENGE));

        questions.add(new IdentificationQuestion(
                725,
                "What number will this code print?\n"
                + "class Wallet {\n"
                + "    private int coins = 5;\n"
                + "    void add(int amount) { if (amount > 0) coins += amount; }\n"
                + "    int getCoins() { return coins; }\n"
                + "}\n"
                + "Wallet w = new Wallet();\n"
                + "w.add(-3);\n"
                + "w.add(4);\n"
                + "System.out.println(w.getCoins());",
                Topic.OOP, Difficulty.BEGINNER,
                "The negative amount is ignored; the valid addition changes 5 to 9.",
                List.of("9")
        ).withChallengeTier(ChallengeTier.CHALLENGE));

        questions.add(new MultipleChoiceQuestion(
                726,
                "What will this code print?\n"
                + "class Base { int value() { return 2; } }\n"
                + "class Child extends Base {\n"
                + "    @Override int value() { return super.value() + 3; }\n"
                + "}\n"
                + "System.out.println(new Child().value());",
                Topic.OOP, Difficulty.BEGINNER,
                "super.value() calls Base.value() and adds 3, resulting in 5.",
                List.of("A. 2", "B. 3", "C. 5", "D. Compilation error"),
                "C"
        ).withChallengeTier(ChallengeTier.CHALLENGE));

        questions.add(new CodeQuestion(
                727,
                "Inside the provided Code class, declare a local class Student "
                + "with a String name field and a constructor Student(String name) "
                + "that stores the supplied name using this.name. "
                + "Create a Student named Mia and print the name.",
                Topic.OOP, Difficulty.BEGINNER,
                "Example inside main: class Student { String name; "
                + "Student(String name) { this.name = name; } } "
                + "Student s = new Student(\"Mia\"); System.out.println(s.name);",
                "Mia",
                List.of("class Student", "Student(String", "this.name", "new Student"),
                List.of()
        ).withChallengeTier(ChallengeTier.CHALLENGE));

        questions.add(new CodeQuestion(
                728,
                "Inside the provided Code class, declare a local class Animal "
                + "with a String sound() method returning Animal, and a local "
                + "class Dog that extends Animal and overrides sound() to return Woof. "
                + "Store a new Dog in an Animal variable and print its sound.",
                Topic.OOP, Difficulty.BEGINNER,
                "Example inside main: class Animal { String sound() { return \"Animal\"; } } "
                + "class Dog extends Animal { @Override String sound() { return \"Woof\"; } } "
                + "Animal pet = new Dog(); System.out.println(pet.sound());",
                "Woof",
                List.of("class Animal", "extends Animal", "@Override", "new Dog"),
                List.of()
        ).withChallengeTier(ChallengeTier.CHALLENGE));

        // ==================================================
        // INTERMEDIATE — preserve existing questions
        // ==================================================

        questions.add(new MultipleChoiceQuestion(
                704,
                "Which access modifier allows a member to be used only inside its declaring class?",
                Topic.OOP, Difficulty.INTERMEDIATE,
                "private restricts access to the class that declares the member.",
                List.of("A. public", "B. protected", "C. private", "D. static"),
                "C"
        ));

        questions.add(new MultipleChoiceQuestion(
                705,
                "What is method overloading?",
                Topic.OOP, Difficulty.INTERMEDIATE,
                "Overloading defines methods with the same name but different parameter lists.",
                List.of("A. Replacing a parent method",
                        "B. Same method name with different parameters",
                        "C. Hiding every field", "D. Creating multiple packages"),
                "B"
        ));

        // ==================================================
        // ADVANCED — preserve existing questions
        // ==================================================

        questions.add(new MultipleChoiceQuestion(
                706,
                "Which OOP principle hides internal data and requires access through methods?",
                Topic.OOP, Difficulty.ADVANCED,
                "Encapsulation protects an object's state behind a controlled public interface.",
                List.of("A. Encapsulation", "B. Iteration",
                        "C. Compilation", "D. Recursion"),
                "A"
        ));

        questions.add(new MultipleChoiceQuestion(
                707,
                "What annotation commonly marks a method that overrides a superclass method?",
                Topic.OOP, Difficulty.ADVANCED,
                "@Override tells the compiler that the method is intended to override an inherited method.",
                List.of("A. @Inherited", "B. @Override", "C. @Super", "D. @Method"),
                "B"
        ));

        return questions;
    }
}
