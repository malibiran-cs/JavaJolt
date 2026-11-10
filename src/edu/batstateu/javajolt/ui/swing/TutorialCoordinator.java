package edu.batstateu.javajolt.ui.swing;

import edu.batstateu.javajolt.entity.TutorialStep;
import edu.batstateu.javajolt.enums.PowerUp;
import edu.batstateu.javajolt.enums.TutorialTrigger;
import edu.batstateu.javajolt.game.GameSession;
import edu.batstateu.javajolt.service.TutorialService;
import edu.batstateu.javajolt.ui.swing.component.TutorialPopup;
import java.awt.Window;
import java.util.List;

// Presents the single tutorial script; MainWindow keeps the quiz loop and countdown.
public class TutorialCoordinator {
    private final TutorialService tutorial;
    private boolean active;
    private boolean replay;
    private TutorialPopup popup;
    private long runVersion;

    public TutorialCoordinator(TutorialService tutorial) {
        this.tutorial = tutorial;
    }

    public GameSession start(boolean replay) {
        stop();
        tutorial.startRun();
        this.replay = replay;
        active = true;
        return tutorial.getSession();
    }

    public void showQuestionSteps(Window owner, int questionIndex, Runnable onWindowClose) {
        if (showSteps(owner, TutorialTrigger.SESSION_START, 0, onWindowClose)) {
            showSteps(owner, TutorialTrigger.BEFORE_QUESTION, questionIndex, onWindowClose);
        }
    }

    public boolean showWrapUp(Window owner, Runnable onWindowClose) {
        return showSteps(owner, TutorialTrigger.RESULTS, 0, onWindowClose);
    }

    private boolean showSteps(Window owner, TutorialTrigger trigger, int questionIndex, Runnable onWindowClose) {
        long version = runVersion;
        List<TutorialStep> due = tutorial.getStepsDue(trigger, questionIndex);
        int number = tutorial.getShownStepCount() - due.size();
        for (TutorialStep step : due) {
            if (!active || version != runVersion || !owner.isDisplayable()) {
                return false;
            }
            TutorialPopup current = new TutorialPopup(owner, step, ++number,
                    tutorial.getTotalStepCount(), replay, onWindowClose);
            popup = current;
            current.setVisible(true);
            current.dispose();
            if (popup == current) {
                popup = null;
            }
            if (version != runVersion) return false;
            if (!current.isConfirmed()) {
                active = false;
                return false;
            }
        }
        return active && owner.isDisplayable();
    }

    public void prepareQuestion() {
        tutorial.prepareQuestion();
    }

    public boolean canUsePowerUp(PowerUp powerUp) {
        return tutorial.canUsePowerUp(powerUp);
    }

    public int getTimeLimitSeconds(int questionIndex) {
        return tutorial.getTimeLimitSeconds(questionIndex);
    }

    public boolean isActive() {
        return active;
    }

    public boolean isReplay() {
        return replay;
    }

    public boolean isPopupOpen() {
        return popup != null;
    }

    public void stop() {
        runVersion++;
        active = false;
        if (popup != null) {
            popup.dispose();
            popup = null;
        }
    }
}
