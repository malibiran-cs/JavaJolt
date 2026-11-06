package edu.batstateu.javajolt.data;

import edu.batstateu.javajolt.Question;
import edu.batstateu.javajolt.enums.ChallengeTier;
import java.util.List;

/** Register all Intermediate/Advanced supplements without replacing the original files. */
public final class ExtraQuestionPacks {
    private ExtraQuestionPacks() {}

    public static void install(List<Question> questions) {
        // Classify the original Intermediate/Advanced questions.
        for (Question question : questions) {
            if (question.getId() == 106) {
                question.withChallengeTier(ChallengeTier.APPLICATION);
                break;
            }
        }
        for (Question question : questions) {
            if (question.getId() == 107) {
                question.withChallengeTier(ChallengeTier.APPLICATION);
                break;
            }
        }
        for (Question question : questions) {
            if (question.getId() == 108) {
                question.withChallengeTier(ChallengeTier.FOUNDATIONAL);
                break;
            }
        }
        for (Question question : questions) {
            if (question.getId() == 109) {
                question.withChallengeTier(ChallengeTier.APPLICATION);
                break;
            }
        }
        for (Question question : questions) {
            if (question.getId() == 110) {
                question.withChallengeTier(ChallengeTier.CHALLENGE);
                break;
            }
        }
        for (Question question : questions) {
            if (question.getId() == 206) {
                question.withChallengeTier(ChallengeTier.APPLICATION);
                break;
            }
        }
        for (Question question : questions) {
            if (question.getId() == 207) {
                question.withChallengeTier(ChallengeTier.FOUNDATIONAL);
                break;
            }
        }
        for (Question question : questions) {
            if (question.getId() == 208) {
                question.withChallengeTier(ChallengeTier.APPLICATION);
                break;
            }
        }
        for (Question question : questions) {
            if (question.getId() == 209) {
                question.withChallengeTier(ChallengeTier.FOUNDATIONAL);
                break;
            }
        }
        for (Question question : questions) {
            if (question.getId() == 210) {
                question.withChallengeTier(ChallengeTier.APPLICATION);
                break;
            }
        }
        for (Question question : questions) {
            if (question.getId() == 211) {
                question.withChallengeTier(ChallengeTier.FOUNDATIONAL);
                break;
            }
        }
        for (Question question : questions) {
            if (question.getId() == 306) {
                question.withChallengeTier(ChallengeTier.FOUNDATIONAL);
                break;
            }
        }
        for (Question question : questions) {
            if (question.getId() == 307) {
                question.withChallengeTier(ChallengeTier.APPLICATION);
                break;
            }
        }
        for (Question question : questions) {
            if (question.getId() == 308) {
                question.withChallengeTier(ChallengeTier.APPLICATION);
                break;
            }
        }
        for (Question question : questions) {
            if (question.getId() == 309) {
                question.withChallengeTier(ChallengeTier.FOUNDATIONAL);
                break;
            }
        }
        for (Question question : questions) {
            if (question.getId() == 404) {
                question.withChallengeTier(ChallengeTier.FOUNDATIONAL);
                break;
            }
        }
        for (Question question : questions) {
            if (question.getId() == 405) {
                question.withChallengeTier(ChallengeTier.APPLICATION);
                break;
            }
        }
        for (Question question : questions) {
            if (question.getId() == 406) {
                question.withChallengeTier(ChallengeTier.APPLICATION);
                break;
            }
        }
        for (Question question : questions) {
            if (question.getId() == 407) {
                question.withChallengeTier(ChallengeTier.FOUNDATIONAL);
                break;
            }
        }
        for (Question question : questions) {
            if (question.getId() == 504) {
                question.withChallengeTier(ChallengeTier.FOUNDATIONAL);
                break;
            }
        }
        for (Question question : questions) {
            if (question.getId() == 505) {
                question.withChallengeTier(ChallengeTier.APPLICATION);
                break;
            }
        }
        for (Question question : questions) {
            if (question.getId() == 506) {
                question.withChallengeTier(ChallengeTier.APPLICATION);
                break;
            }
        }
        for (Question question : questions) {
            if (question.getId() == 507) {
                question.withChallengeTier(ChallengeTier.FOUNDATIONAL);
                break;
            }
        }
        for (Question question : questions) {
            if (question.getId() == 604) {
                question.withChallengeTier(ChallengeTier.FOUNDATIONAL);
                break;
            }
        }
        for (Question question : questions) {
            if (question.getId() == 605) {
                question.withChallengeTier(ChallengeTier.FOUNDATIONAL);
                break;
            }
        }
        for (Question question : questions) {
            if (question.getId() == 606) {
                question.withChallengeTier(ChallengeTier.FOUNDATIONAL);
                break;
            }
        }
        for (Question question : questions) {
            if (question.getId() == 607) {
                question.withChallengeTier(ChallengeTier.FOUNDATIONAL);
                break;
            }
        }
        for (Question question : questions) {
            if (question.getId() == 704) {
                question.withChallengeTier(ChallengeTier.FOUNDATIONAL);
                break;
            }
        }
        for (Question question : questions) {
            if (question.getId() == 705) {
                question.withChallengeTier(ChallengeTier.FOUNDATIONAL);
                break;
            }
        }
        for (Question question : questions) {
            if (question.getId() == 706) {
                question.withChallengeTier(ChallengeTier.FOUNDATIONAL);
                break;
            }
        }
        for (Question question : questions) {
            if (question.getId() == 707) {
                question.withChallengeTier(ChallengeTier.FOUNDATIONAL);
                break;
            }
        }
        // Add new questions, preserving every existing Beginner question.
        questions.addAll(VariablesAdvancedExpansion.getQuestions());
        questions.addAll(OperatorsIntermediateExpansion.getQuestions());
        questions.addAll(OperatorsAdvancedExpansion.getQuestions());
        questions.addAll(DecisionsIntermediateExpansion.getQuestions());
        questions.addAll(DecisionsAdvancedExpansion.getQuestions());
        questions.addAll(LoopsIntermediateExpansion.getQuestions());
        questions.addAll(LoopsAdvancedExpansion.getQuestions());
        questions.addAll(ArraysIntermediateExpansion.getQuestions());
        questions.addAll(ArraysAdvancedExpansion.getQuestions());
        questions.addAll(ExceptionHandlingIntermediateExpansion.getQuestions());
        questions.addAll(ExceptionHandlingAdvancedExpansion.getQuestions());
        questions.addAll(OOPIntermediateExpansion.getQuestions());
        questions.addAll(OOPAdvancedExpansion.getQuestions());
    }
}
