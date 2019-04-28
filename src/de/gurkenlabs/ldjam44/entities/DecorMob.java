package de.gurkenlabs.ldjam44.entities;

import java.util.Random;

import de.gurkenlabs.litiengine.Game;
import de.gurkenlabs.litiengine.annotation.AnimationInfo;
import de.gurkenlabs.litiengine.annotation.CombatInfo;
import de.gurkenlabs.litiengine.annotation.MovementInfo;
import de.gurkenlabs.litiengine.entities.Creature;
import de.gurkenlabs.litiengine.physics.IMovementController;
import de.gurkenlabs.litiengine.physics.MovementController;

@CombatInfo(hitpoints = 1)
@AnimationInfo(spritePrefix = { "decormob-cockroach" })
public class DecorMob extends Creature {

  public DecorMob(String spritePrefix) {
    super(spritePrefix);

    this.getVelocity().setBaseValue(Float.valueOf(2f));
    this.setController(IMovementController.class, new ShyDecorMobMovementController(this));
  }

  private class ShyDecorMobMovementController extends MovementController<DecorMob> {
    private int angle;
    private long lastAngleChange;
    private long nextAngleChange;

    public ShyDecorMobMovementController(final DecorMob decorMob) {
      super(decorMob);
      this.calculateNextAngleChange();
    }

    public void calculateNextAngleChange() {
      this.nextAngleChange = new Random().nextInt(3000) + (long) 2000;
    }

    @Override
    public void update() {
      super.update();
      if (Game.world().environment() == null || DecorMob.this.isDead()) {
        return;
      }
      final long currentTick = Game.loop().getTicks();
      final long timeSinceLastAngleChange = Game.time().since(this.lastAngleChange);
      if (this.angle == 0 || timeSinceLastAngleChange > this.nextAngleChange) {
        final Random rand = new Random();
        this.angle = rand.nextInt(360);
        this.lastAngleChange = currentTick;
        this.calculateNextAngleChange();
      }

      final float pixelsPerTick = this.getEntity().getTickVelocity();
      Game.physics().move(this.getEntity(), this.angle, pixelsPerTick);
    }
  }
}
