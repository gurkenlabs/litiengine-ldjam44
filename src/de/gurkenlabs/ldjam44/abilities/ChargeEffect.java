package de.gurkenlabs.ldjam44.abilities;

import de.gurkenlabs.ldjam44.entities.Player;
import de.gurkenlabs.litiengine.Game;
import de.gurkenlabs.litiengine.abilities.Ability;
import de.gurkenlabs.litiengine.abilities.effects.Effect;
import de.gurkenlabs.litiengine.abilities.effects.EffectTarget;
import de.gurkenlabs.litiengine.entities.ICombatEntity;
import de.gurkenlabs.litiengine.util.geom.GeometricUtilities;

public class ChargeEffect extends Effect {
  private static int damage = 1;
  private double angle;

  private boolean wasHit;

  public ChargeEffect(final Ability ability) {
    super(ability, EffectTarget.EXECUTINGENTITY);
  }

  @Override
  public void update() {
    final long deltaTime = Game.loop().getDeltaTime();
    final double maxPixelsPerTick = this.getAbility().getAttributes().getValue().getCurrentValue() / 1000.0 * Math.min(deltaTime, 50);

    if (!wasHit) {
      for (ICombatEntity ent : Game.world().environment().findCombatEntities(this.getAbility().getExecutor().getHitBox(), e -> e.equals(Player.instance()))) {
        ent.hit(damage, this.getAbility());
        wasHit = true;
      }
    }

    Game.physics().move(this.getAbility().getExecutor(), this.angle, maxPixelsPerTick);

    super.update();
  }

  public double getAngle() {
    return this.angle;
  }

  @Override
  protected void apply(final ICombatEntity entity) {
    this.angle = GeometricUtilities.calcRotationAngleInDegrees(this.getAbility().getExecutor().getCenter(), Player.instance().getCenter());
    wasHit = false;
    super.apply(entity);
  }
}
