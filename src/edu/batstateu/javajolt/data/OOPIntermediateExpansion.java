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

/** Additional OOP intermediate questions. Does not replace existing banks. */
public final class OOPIntermediateExpansion {
    private OOPIntermediateExpansion() {}

    public static List<Question> getQuestions() {
        List<Question> questions = new ArrayList<>();

        // FOUNDATIONAL
        questions.add(new MultipleChoiceQuestion(
                730,
                "What is constructor overloading?",
                Topic.OOP, Difficulty.INTERMEDIATE,
                "Overloaded constructors have distinct parameter signatures.",
                List.of(
                        "A. Two classes with the same name in one package",
                        "B. A private method in a superclass",
                        "C. Multiple constructors with different parameter lists",
                        "D. A constructor overriding a method"
                ), "C"
        ).withChallengeTier(ChallengeTier.FOUNDATIONAL));

        questions.add(new MultipleChoiceQuestion(
                731,
                "Which keyword refers to the superclass implementation inside a subclass?",
                Topic.OOP, Difficulty.INTERMEDIATE,
                "super accesses superclass constructors and accessible members.",
                List.of(
                        "A. this",
                        "B. extends",
                        "C. static",
                        "D. super"
                ), "D"
        ).withChallengeTier(ChallengeTier.FOUNDATIONAL));

        questions.add(new MultipleChoiceQuestion(
                732,
                "Which keyword makes a field shared across all instances of a class?",
                Topic.OOP, Difficulty.INTERMEDIATE,
                "A static field belongs to the class rather than each instance.",
                List.of(
                        "A. static",
                        "B. private",
                        "C. final",
                        "D. abstract"
                ), "A"
        ).withChallengeTier(ChallengeTier.FOUNDATIONAL));

        questions.add(new MultipleChoiceQuestion(
                733,
                "What is method overriding?",
                Topic.OOP, Difficulty.INTERMEDIATE,
                "Overriding replaces inherited instance behavior for a compatible signature.",
                List.of(
                        "A. A field is initialized twice",
                        "B. A subclass supplies an implementation with the same overriding signature",
                        "C. Two methods have the same name with different parameters",
                        "D. Two constructors have the same name"
                ), "B"
        ).withChallengeTier(ChallengeTier.FOUNDATIONAL));

        questions.add(new MultipleChoiceQuestion(
                734,
                "What does the final modifier prevent when applied to a class?",
                Topic.OOP, Difficulty.INTERMEDIATE,
                "A final class cannot be extended.",
                List.of(
                        "A. Calling its methods",
                        "B. Adding static members",
                        "C. Subclassing it",
                        "D. Constructing objects from it"
                ), "C"
        ).withChallengeTier(ChallengeTier.FOUNDATIONAL));

        questions.add(new MultipleChoiceQuestion(
                735,
                "Which Java relationship is modeled by composition?",
                Topic.OOP, Difficulty.INTERMEDIATE,
                "Composition models has-a by storing member objects.",
                List.of(
                        "A. A Dog extends Animal",
                        "B. A method overloads another",
                        "C. A class implements Runnable",
                        "D. A Car contains an Engine field"
                ), "D"
        ).withChallengeTier(ChallengeTier.FOUNDATIONAL));

        // APPLICATION
        questions.add(new MultipleChoiceQuestion(
                736,
                "What prints?\nclass Box {int size; Box(int n){size=n;}}\nSystem.out.println(new Box(7).size);",
                Topic.OOP, Difficulty.INTERMEDIATE,
                "The constructor stores seven in the instance field.",
                List.of(
                        "A. 7",
                        "B. 0",
                        "C. 1",
                        "D. Compilation error"
                ), "A"
        ).withChallengeTier(ChallengeTier.APPLICATION));

        questions.add(new MultipleChoiceQuestion(
                737,
                "What prints?\nclass Counter {static int total=0; Counter(){total++;}}\nnew Counter();new Counter();System.out.println(Counter.total);",
                Topic.OOP, Difficulty.INTERMEDIATE,
                "Each constructor increments the same static counter.",
                List.of(
                        "A. Compilation error",
                        "B. 2",
                        "C. 1",
                        "D. 0"
                ), "B"
        ).withChallengeTier(ChallengeTier.APPLICATION));

        questions.add(new MultipleChoiceQuestion(
                738,
                "What prints?\nclass A {int value(){return 1;}}\nclass B extends A {int value(){return 3;}}\nA x = new B();System.out.println(x.value());",
                Topic.OOP, Difficulty.INTERMEDIATE,
                "Dynamic dispatch calls B.value on a B object.",
                List.of(
                        "A. 0",
                        "B. Compilation error",
                        "C. 3",
                        "D. 1"
                ), "C"
        ).withChallengeTier(ChallengeTier.APPLICATION));

        questions.add(new IdentificationQuestion(
                739,
                "What prints?\nclass Item {private int price=5; int getPrice(){return price;}}\nItem item=new Item();System.out.println(item.getPrice());",
                Topic.OOP, Difficulty.INTERMEDIATE,
                "An accessor method returns the private field value.",
                List.of("5")
        ).withChallengeTier(ChallengeTier.APPLICATION));

        questions.add(new MultipleChoiceQuestion(
                740,
                "What prints?\nclass Person {String name; Person(){this(\"Guest\");} Person(String n){name=n;}}\nSystem.out.println(new Person().name);",
                Topic.OOP, Difficulty.INTERMEDIATE,
                "The no-argument constructor chains to the String constructor.",
                List.of(
                        "A. Guest",
                        "B. null",
                        "C. Person",
                        "D. Compilation error"
                ), "A"
        ).withChallengeTier(ChallengeTier.APPLICATION));

        questions.add(new MultipleChoiceQuestion(
                741,
                "What prints?\nclass Base {int n=2;}\nclass Child extends Base {}\nSystem.out.println(new Child().n);",
                Topic.OOP, Difficulty.INTERMEDIATE,
                "Child inherits the accessible n field.",
                List.of(
                        "A. Compilation error",
                        "B. 2",
                        "C. 0",
                        "D. null"
                ), "B"
        ).withChallengeTier(ChallengeTier.APPLICATION));

        questions.add(new MultipleChoiceQuestion(
                742,
                "What prints?\nclass Animal {String speak(){return \"A\";}}\nclass Cat extends Animal {String speak(){return \"C\";}}\nSystem.out.println(new Cat().speak());",
                Topic.OOP, Difficulty.INTERMEDIATE,
                "Cat overrides the inherited method.",
                List.of(
                        "A. AC",
                        "B. Compilation error",
                        "C. C",
                        "D. A"
                ), "C"
        ).withChallengeTier(ChallengeTier.APPLICATION));

        questions.add(new MultipleChoiceQuestion(
                743,
                "What prints?\nclass Player {int hp=10; void damage(){hp-=3;}}\nPlayer p=new Player();Player alias=p;alias.damage();\nSystem.out.println(p.hp);",
                Topic.OOP, Difficulty.INTERMEDIATE,
                "Both references point to the same mutable Player object.",
                List.of(
                        "A. 10",
                        "B. 3",
                        "C. Compilation error",
                        "D. 7"
                ), "D"
        ).withChallengeTier(ChallengeTier.APPLICATION));

        // CHALLENGE
        questions.add(new MultipleChoiceQuestion(
                744,
                "What prints?\nclass A {int x=1; int get(){return x;}}\nclass B extends A {int x=2;}\nA a=new B();System.out.println(a.x+\":\"+a.get());",
                Topic.OOP, Difficulty.INTERMEDIATE,
                "Fields are accessed using declared reference type A; A.get returns A.x.",
                List.of(
                        "A. 1:1",
                        "B. 2:2",
                        "C. 1:2",
                        "D. 2:1"
                ), "A"
        ).withChallengeTier(ChallengeTier.CHALLENGE));

        questions.add(new MultipleChoiceQuestion(
                745,
                "What prints?\nclass A {int f(int n){return n+1;} int f(int a,int b){return a+b;}}\nA a=new A();System.out.println(a.f(2)+a.f(3,4));",
                Topic.OOP, Difficulty.INTERMEDIATE,
                "Overload calls return three and seven for a total of ten.",
                List.of(
                        "A. 9",
                        "B. 10",
                        "C. 7",
                        "D. 8"
                ), "B"
        ).withChallengeTier(ChallengeTier.CHALLENGE));

        questions.add(new IdentificationQuestion(
                746,
                "What prints?\nclass Counter {static int x=0; Counter(){x++;}}\nnew Counter(); new Counter(); new Counter();\nSystem.out.println(Counter.x);",
                Topic.OOP, Difficulty.INTERMEDIATE,
                "All three constructed objects increment the same static field.",
                List.of("3")
        ).withChallengeTier(ChallengeTier.CHALLENGE));

        questions.add(new MultipleChoiceQuestion(
                747,
                "What prints?\nclass Animal{String sound(){return \"a\";}}\nclass Dog extends Animal{String sound(){return \"d\";}}\nAnimal a=new Dog();System.out.println(a.sound());",
                Topic.OOP, Difficulty.INTERMEDIATE,
                "The runtime object type Dog determines the overridden method.",
                List.of(
                        "A. a",
                        "B. ad",
                        "C. Compilation error",
                        "D. d"
                ), "D"
        ).withChallengeTier(ChallengeTier.CHALLENGE));

        questions.add(new MultipleChoiceQuestion(
                748,
                "Which declaration fails to compile because it attempts to override a final method?",
                Topic.OOP, Difficulty.INTERMEDIATE,
                "A final instance method cannot be overridden with the same signature.",
                List.of(
                        "A. class Child extends Base { void run(){} } when Base declares final void run(){}",
                        "B. class Child extends Base { void walk(){} } when Base declares void run(){}",
                        "C. class Child extends Base {} when Base declares void run(){}",
                        "D. class Child extends Base { void run(int x){} } when Base declares final void run(){}"
                ), "A"
        ).withChallengeTier(ChallengeTier.CHALLENGE));

        questions.add(new IdentificationQuestion(
                749,
                "What prints?\nclass Product{int price=2; void set(int price){this.price=price;}}\nProduct p=new Product();p.set(9);System.out.println(p.price);",
                Topic.OOP, Difficulty.INTERMEDIATE,
                "this.price distinguishes the instance field from the parameter.",
                List.of("9")
        ).withChallengeTier(ChallengeTier.CHALLENGE));

        questions.add(new CodeQuestion(
                750,
                "Inside the supplied Code class, declare a local class Circle with a double radius field and constructor Circle(double radius). Create radius 3.0 and print radius * radius.",
                Topic.OOP, Difficulty.INTERMEDIATE,
                "The constructor initializes radius and 3.0 squared is 9.0.",
                "9.0",
                List.of("class Circle", "Circle(double", "this.radius", "System.out.println"),
                List.of()
        ).withChallengeTier(ChallengeTier.CHALLENGE));

        questions.add(new CodeQuestion(
                751,
                "Inside the supplied Code class, declare a nested static class Counter containing a static int total initialized to zero; its constructor increments total. Create two objects in main and print total.",
                Topic.OOP, Difficulty.INTERMEDIATE,
                "A static field belongs to the nested class and counts two constructions.",
                "2",
                List.of("class Counter", "static int", "new Counter", "System.out.println"),
                List.of()
        ).withChallengeTier(ChallengeTier.CHALLENGE));

        return questions;
    }
}
