package de.gurkenlabs.ldjam44.entities;

import de.gurkenlabs.litiengine.Game;
import de.gurkenlabs.litiengine.IUpdateable;
import de.gurkenlabs.litiengine.annotation.AnimationInfo;
import de.gurkenlabs.litiengine.annotation.CollisionInfo;
import de.gurkenlabs.litiengine.entities.Prop;
import de.gurkenlabs.litiengine.resources.Resources;

@CollisionInfo(collision = false)
@AnimationInfo(spritePrefix = "prop-pot")
public class HealthPot extends Prop implements IUpdateable {

  public HealthPot(String spritesheetName) {
    super(spritesheetName);
  }

  @Override
  public void update() {
    if (this.getBoundingBox().intersects(Player.instance().getCollisionBox()) && Player.instance().getHitPoints().getCurrentValue() < Player.instance().getHitPoints().getMaxValue()) {
      Game.world().environment().remove(this);
      Player.instance().getHitPoints().setToMaxValue();
      Game.audio().playSound(Resources.sounds().get("pot"));
    }
  }
}
