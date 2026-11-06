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

/** Additional OOP advanced questions. Does not replace existing banks. */
public final class OOPAdvancedExpansion {
    private OOPAdvancedExpansion() {}

    public static List<Question> getQuestions() {
        List<Question> questions = new ArrayList<>();

        // FOUNDATIONAL
        questions.add(new MultipleChoiceQuestion(
                752,
                "What is an abstract class in Java?",
                Topic.OOP, Difficulty.ADVANCED,
                "An abstract class can offer shared implementation but cannot be directly constructed.",
                List.of(
                        "A. A class that may contain abstract methods and cannot be instantiated directly",
                        "B. A class that cannot have constructors",
                        "C. A class whose methods must all be static",
                        "D. A class that cannot contain fields"
                ), "A"
        ).withChallengeTier(ChallengeTier.FOUNDATIONAL));

        questions.add(new MultipleChoiceQuestion(
                753,
                "What does an interface primarily declare?",
                Topic.OOP, Difficulty.ADVANCED,
                "An interface defines an implementation contract for classes.",
                List.of(
                        "A. A replacement for the JVM",
                        "B. A contract of behaviors for implementing classes",
                        "C. Only object data with constructors",
                        "D. A private nested package"
                ), "B"
        ).withChallengeTier(ChallengeTier.FOUNDATIONAL));

        questions.add(new MultipleChoiceQuestion(
                754,
                "What is dynamic method dispatch?",
                Topic.OOP, Difficulty.ADVANCED,
                "Java dispatches overridden instance methods using the actual object.",
                List.of(
                        "A. Invoking a static method based on runtime type",
                        "B. Calling a constructor again",
                        "C. Choosing an overridden instance method based on runtime object type",
                        "D. Choosing fields using the runtime class"
                ), "C"
        ).withChallengeTier(ChallengeTier.FOUNDATIONAL));

        questions.add(new MultipleChoiceQuestion(
                755,
                "Which relationship is represented by implements?",
                Topic.OOP, Difficulty.ADVANCED,
                "implements declares that a class supports an interface.",
                List.of(
                        "A. A class inherits another class",
                        "B. A method overrides its constructor",
                        "C. A variable is initialized",
                        "D. A class fulfills an interface contract"
                ), "D"
        ).withChallengeTier(ChallengeTier.FOUNDATIONAL));

        questions.add(new MultipleChoiceQuestion(
                756,
                "What does the Liskov substitution principle suggest?",
                Topic.OOP, Difficulty.ADVANCED,
                "Well-behaved subtypes honor their supertype contracts.",
                List.of(
                        "A. A subtype should be usable where its supertype is expected without breaking promised behavior",
                        "B. Every subclass must add fields",
                        "C. All constructors must have identical arguments",
                        "D. Interfaces may not extend other interfaces"
                ), "A"
        ).withChallengeTier(ChallengeTier.FOUNDATIONAL));

        questions.add(new MultipleChoiceQuestion(
                757,
                "Which statement about static methods is true?",
                Topic.OOP, Difficulty.ADVANCED,
                "Static method resolution depends on the declared type, not runtime object dispatch.",
                List.of(
                        "A. They cannot be declared in classes",
                        "B. They are hidden, not overridden through virtual dispatch",
                        "C. They are always dynamically dispatched",
                        "D. They require a new object for invocation"
                ), "B"
        ).withChallengeTier(ChallengeTier.FOUNDATIONAL));

        // APPLICATION
        questions.add(new MultipleChoiceQuestion(
                758,
                "What prints?\ninterface Flyer {String move();}\nclass Bird implements Flyer {public String move(){return \"Fly\";}}\nFlyer f=new Bird();System.out.println(f.move());",
                Topic.OOP, Difficulty.ADVANCED,
                "Bird implements the interface method.",
                List.of(
                        "A. Flyer",
                        "B. Compilation error",
                        "C. Fly",
                        "D. Bird"
                ), "C"
        ).withChallengeTier(ChallengeTier.APPLICATION));

        questions.add(new MultipleChoiceQuestion(
                759,
                "What prints?\nabstract class Shape {abstract int area();}\nclass Square extends Shape {int area(){return 4;}}\nShape s=new Square();System.out.println(s.area());",
                Topic.OOP, Difficulty.ADVANCED,
                "The Square implementation fulfills the abstract method and is dynamically dispatched.",
                List.of(
                        "A. 0",
                        "B. Shape",
                        "C. Compilation error",
                        "D. 4"
                ), "D"
        ).withChallengeTier(ChallengeTier.APPLICATION));

        questions.add(new IdentificationQuestion(
                760,
                "What prints?\nclass A {int f(){return 2;}}\nclass B extends A {int f(){return super.f()+3;}}\nSystem.out.println(new B().f());",
                Topic.OOP, Difficulty.ADVANCED,
                "The subclass calls super.f() returning two and adds three.",
                List.of("5")
        ).withChallengeTier(ChallengeTier.APPLICATION));

        questions.add(new MultipleChoiceQuestion(
                761,
                "What prints?\nclass A {static String name(){return \"A\";}}\nclass B extends A {static String name(){return \"B\";}}\nA ref=new B();System.out.println(ref.name());",
                Topic.OOP, Difficulty.ADVANCED,
                "Static methods are hidden and selected by declared reference type.",
                List.of(
                        "A. Compilation error",
                        "B. A",
                        "C. B",
                        "D. AB"
                ), "B"
        ).withChallengeTier(ChallengeTier.APPLICATION));

        questions.add(new MultipleChoiceQuestion(
                762,
                "What prints?\nclass A {int x=1;}\nclass B extends A {int x=2;}\nA ref=new B();System.out.println(ref.x);",
                Topic.OOP, Difficulty.ADVANCED,
                "Fields are resolved by reference type rather than overridden dynamically.",
                List.of(
                        "A. 0",
                        "B. Compilation error",
                        "C. 1",
                        "D. 2"
                ), "C"
        ).withChallengeTier(ChallengeTier.APPLICATION));

        questions.add(new MultipleChoiceQuestion(
                763,
                "What prints?\ninterface Greeter {default String hi(){return \"Hello\";}}\nclass Person implements Greeter {}\nSystem.out.println(new Person().hi());",
                Topic.OOP, Difficulty.ADVANCED,
                "The class inherits the interface default method.",
                List.of(
                        "A. null",
                        "B. Person",
                        "C. Compilation error",
                        "D. Hello"
                ), "D"
        ).withChallengeTier(ChallengeTier.APPLICATION));

        questions.add(new IdentificationQuestion(
                764,
                "What prints?\nclass A {int f(int x){return x+1;}}\nclass B extends A {int f(int x){return x*2;}}\nA ref=new B();System.out.println(ref.f(4));",
                Topic.OOP, Difficulty.ADVANCED,
                "The overridden B.f method doubles four.",
                List.of("8")
        ).withChallengeTier(ChallengeTier.APPLICATION));

        questions.add(new MultipleChoiceQuestion(
                765,
                "What prints?\nclass Base {}\nclass Derived extends Base {}\nBase ref=new Derived();\nSystem.out.println(ref instanceof Derived);",
                Topic.OOP, Difficulty.ADVANCED,
                "The runtime object is a Derived, so instanceof returns true.",
                List.of(
                        "A. Compilation error",
                        "B. true",
                        "C. false",
                        "D. null"
                ), "B"
        ).withChallengeTier(ChallengeTier.APPLICATION));

        // CHALLENGE
        questions.add(new MultipleChoiceQuestion(
                766,
                "What prints?\nclass A {String f(){return \"A\";}}\nclass B extends A {String f(){return \"B\";}}\nclass C extends B {}\nA x=new C();System.out.println(x.f());",
                Topic.OOP, Difficulty.ADVANCED,
                "C inherits B override, which is selected at runtime.",
                List.of(
                        "A. C",
                        "B. Compilation error",
                        "C. B",
                        "D. A"
                ), "C"
        ).withChallengeTier(ChallengeTier.CHALLENGE));

        questions.add(new MultipleChoiceQuestion(
                767,
                "What prints?\nclass A {static int count=0;A(){count++;}}\nclass B extends A {B(){super();}}\nnew B();new B();System.out.println(A.count);",
                Topic.OOP, Difficulty.ADVANCED,
                "Each B constructor invokes A constructor once, incrementing shared count.",
                List.of(
                        "A. 0",
                        "B. 1",
                        "C. 4",
                        "D. 2"
                ), "D"
        ).withChallengeTier(ChallengeTier.CHALLENGE));

        questions.add(new IdentificationQuestion(
                768,
                "What prints?\nclass A {int n(){return 3;}}\nclass B extends A {int n(){return super.n()*2;}}\nSystem.out.println(new B().n());",
                Topic.OOP, Difficulty.ADVANCED,
                "The overridden method calls superclass n=3 then doubles the result.",
                List.of("6")
        ).withChallengeTier(ChallengeTier.CHALLENGE));

        questions.add(new MultipleChoiceQuestion(
                769,
                "What happens?\nabstract class A { abstract void go(); }\nclass B extends A { }",
                Topic.OOP, Difficulty.ADVANCED,
                "A nonabstract subclass must implement inherited abstract methods.",
                List.of(
                        "A. A is instantiated",
                        "B. Compilation error",
                        "C. B inherits a working go method",
                        "D. B is automatically abstract"
                ), "B"
        ).withChallengeTier(ChallengeTier.CHALLENGE));

        questions.add(new MultipleChoiceQuestion(
                770,
                "What prints?\nclass A {int x=2;int f(){return x;}}\nclass B extends A {int x=5;int f(){return x+super.x;}}\nA a=new B();System.out.println(a.f());",
                Topic.OOP, Difficulty.ADVANCED,
                "Runtime dispatch selects B.f, which adds B.x=5 and A.x=2.",
                List.of(
                        "A. 5",
                        "B. Compilation error",
                        "C. 7",
                        "D. 2"
                ), "C"
        ).withChallengeTier(ChallengeTier.CHALLENGE));

        questions.add(new IdentificationQuestion(
                771,
                "What prints?\ninterface A {default int f(){return 2;}}\nclass B implements A {public int f(){return A.super.f()+5;}}\nSystem.out.println(new B().f());",
                Topic.OOP, Difficulty.ADVANCED,
                "The override calls the interface default returning two and adds five.",
                List.of("7")
        ).withChallengeTier(ChallengeTier.CHALLENGE));

        questions.add(new CodeQuestion(
                772,
                "Inside the supplied Code class, declare a nested interface Greeter with String greet(); and a nested static class Robot implementing it to return Hello. In main, create a Greeter reference to Robot and print greet().",
                Topic.OOP, Difficulty.ADVANCED,
                "A nested interface and implementing nested class are declared at class scope, outside main.",
                "Hello",
                List.of("interface Greeter", "implements Greeter", "new Robot", "System.out.println"),
                List.of()
        ).withChallengeTier(ChallengeTier.CHALLENGE));

        questions.add(new CodeQuestion(
                773,
                "Inside the supplied Code class, declare a nested static abstract class Shape with abstract int area(); and a nested static class Square extending Shape and overriding area() to return 16. In main, store new Square in a Shape reference and print area().",
                Topic.OOP, Difficulty.ADVANCED,
                "Declare nested classes outside main; dynamic dispatch selects Square.area().",
                "16",
                List.of("abstract class Shape", "extends Shape", "new Square", "System.out.println"),
                List.of()
        ).withChallengeTier(ChallengeTier.CHALLENGE));

        return questions;
    }
}
