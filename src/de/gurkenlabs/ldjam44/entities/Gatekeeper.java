package de.gurkenlabs.ldjam44.entities;

import de.gurkenlabs.ldjam44.GameManager;
import de.gurkenlabs.ldjam44.entities.Player.PlayerState;
import de.gurkenlabs.litiengine.Game;
import de.gurkenlabs.litiengine.annotation.CollisionInfo;
import de.gurkenlabs.litiengine.annotation.CombatInfo;
import de.gurkenlabs.litiengine.annotation.EntityInfo;
import de.gurkenlabs.litiengine.entities.Creature;
import de.gurkenlabs.litiengine.environment.Environment;
import de.gurkenlabs.litiengine.gui.SpeechBubble;

@EntityInfo(width = 11, height = 20)
@CollisionInfo(collisionBoxWidth = 5, collisionBoxHeight = 8, collision = true)
@CombatInfo(isIndestructible = true)
public class Gatekeeper extends Creature {
  public static final String MESSAGE_FINISH = "FINISH";

  private int requiredSlaves;

  public Gatekeeper() {
    this.addMessageListener(l -> {
      if (l.getMessage() == null) {
        return;
      }

      if (l.getMessage().equals(MESSAGE_FINISH)) {
        String text = "YOU REQUIRE AT LEAST " + this.requiredSlaves + " SLAVES TO PASS!";
        if (GameManager.getOwnSlaveCount() >= this.getRequiredSlaves()) {
          text = "WELL DONE! YOU CAN PASS!";
          // TODO: IMPLEMENT TRANSITION
        }

        SpeechBubble.create(this, text, GameManager.SPEECH_BUBBLE_APPEARANCE, GameManager.SPEECH_BUBBLE_FONT);
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
      SpeechBubble.create(this, "WELCOME! NOONE HAS EVER HAD MORE THAN " + this.getRequiredSlaves() + " SLAVES AROUND HERE!", GameManager.SPEECH_BUBBLE_APPEARANCE, GameManager.SPEECH_BUBBLE_FONT);
      Game.loop().perform(4000, () -> {
        Player.instance().setState(PlayerState.CONTROLLABLE);
      });
    });
  }
}
