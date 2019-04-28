package de.gurkenlabs.ldjam44.entities;

import java.text.MessageFormat;

import de.gurkenlabs.ldjam44.GameManager;
import de.gurkenlabs.ldjam44.entities.Player.PlayerState;
import de.gurkenlabs.litiengine.Game;
import de.gurkenlabs.litiengine.annotation.CollisionInfo;
import de.gurkenlabs.litiengine.annotation.CombatInfo;
import de.gurkenlabs.litiengine.annotation.EntityInfo;
import de.gurkenlabs.litiengine.entities.Creature;
import de.gurkenlabs.litiengine.environment.Environment;
import de.gurkenlabs.litiengine.gui.SpeechBubble;
import de.gurkenlabs.litiengine.gui.SpeechBubbleListener;
import de.gurkenlabs.litiengine.resources.Resources;
import de.gurkenlabs.litiengine.util.ArrayUtilities;

@EntityInfo(width = 11, height = 20)
@CollisionInfo(collisionBoxWidth = 5, collisionBoxHeight = 8, collision = true)
@CombatInfo(isIndestructible = true)
public class Gatekeeper extends Creature {
  public static final String MESSAGE_FINISH = "FINISH";
  private static final String[] rudeLines;
  private static final String[] goalLines;

  private int requiredSlaves;
  private String nextLevel;

  static {
    rudeLines = Resources.strings().getList("rude.txt");
    goalLines = Resources.strings().getList("goal.txt");
  }

  public Gatekeeper() {
    this.addMessageListener(l -> {
      if (l.getMessage() == null) {
        return;
      }

      if (l.getMessage().equals(MESSAGE_FINISH)) {
        String text = "You need to trade me " + this.requiredSlaves + " slaves to advance!";
        if (GameManager.getOwnSlaveCount() >= this.getRequiredSlaves()) {
          text = "WELL DONE! Now I can take you to " + GameManager.getCity(this.getNextLevel()) + ".";
          Game.audio().playSound(Resources.sounds().get("success"));
          SpeechBubble bubble = SpeechBubble.create(this, text, GameManager.SPEECH_BUBBLE_APPEARANCE, GameManager.SPEECH_BUBBLE_FONT);
          bubble.setTextDisplayTime(4000);
          Player.instance().setState(PlayerState.LOCKED);

          bubble.addListener(new SpeechBubbleListener() {
            @Override
            public void hidden() {

              // remove player before unloading the environment or the instance's animation controller will be disposed
              Game.world().environment().remove(Player.instance());
              Game.world().loadEnvironment(getNextLevel());
            }
          });
        } else {
          SpeechBubble.create(this, text, GameManager.SPEECH_BUBBLE_APPEARANCE, GameManager.SPEECH_BUBBLE_FONT);
        }
      }

    });
  }

  public int getRequiredSlaves() {
    return requiredSlaves;
  }

  public void setRequiredSlaves(int requiredSlaves) {
    this.requiredSlaves = requiredSlaves;
  }

  @Override
  public void loaded(Environment environment) {
    super.loaded(environment);

    Game.loop().perform(1000, () -> {

      String rude = ArrayUtilities.getRandom(rudeLines);
      String goal = MessageFormat.format(ArrayUtilities.getRandom(goalLines), GameManager.getCity(Game.world().environment().getMap().getName()), this.getRequiredSlaves());
      SpeechBubble bubble = SpeechBubble.create(this, rude + " " + goal, GameManager.SPEECH_BUBBLE_APPEARANCE, GameManager.SPEECH_BUBBLE_FONT);
      bubble.setTextDisplayTime(5000);
      bubble.addListener(new SpeechBubbleListener() {
        @Override
        public void hidden() {
          Player.instance().setState(PlayerState.CONTROLLABLE);
        }
      });
    });
  }

  public String getNextLevel() {
    return nextLevel;
  }

  public void setNextLevel(String nextLevel) {
    this.nextLevel = nextLevel;
  }
}
