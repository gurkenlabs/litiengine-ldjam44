package de.gurkenlabs.ldjam44.entities;

import java.awt.Color;

import de.gurkenlabs.ldjam44.GameManager;
import de.gurkenlabs.litiengine.annotation.AnimationInfo;
import de.gurkenlabs.litiengine.entities.Creature;
import de.gurkenlabs.litiengine.environment.Environment;
import de.gurkenlabs.litiengine.graphics.CreatureShadowImageEffect;
import de.gurkenlabs.litiengine.gui.SpeechBubble;
import de.gurkenlabs.litiengine.gui.SpeechBubbleAppearance;

@AnimationInfo(spritePrefix = { "enemy_gold", "enemy_silver", "enemy_leather" })
public class Enemy extends Creature {
  public static String SLAVE_TRIGGER = "GIVE ME SLAVES";

  public Enemy() {
    this.addMessageListener(l -> {
      if (l.getMessage() == null) {
        return;
      }

      if (l.getMessage().equals(SLAVE_TRIGGER)) {
        SpeechBubbleAppearance appearance = new SpeechBubbleAppearance(new Color(16, 20, 19), new Color(255, 255, 255, 150), new Color(16, 20, 19), 5);
        appearance.setBackgroundColor2(new Color(255, 255, 255, 220));
        SpeechBubble.create(this, "FEEEEELL MY WRATH!!!!!", appearance, GameManager.SPEECH_BUBBLE_FONT);
      }
    });
  }

  @Override
  public void loaded(Environment environment) {
    super.loaded(environment);

    this.getAnimationController().add(new CreatureShadowImageEffect(this, new Color(0, 0, 0, 150)));
  }
}
