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

  private long lastHit;

  public ChargeEffect(final Ability ability) {
    super(ability, EffectTarget.EXECUTINGENTITY);
  }

  @Override
  public void update() {
    final long deltaTime = Game.loop().getDeltaTime();
    final double maxPixelsPerTick = this.getAbility().getAttributes().getValue().getCurrentValue() / 1000.0 * Math.min(deltaTime, 50);

    final int hitDelay = 200;
    for (ICombatEntity ent : Game.world().environment().findCombatEntities(this.getAbility().getExecutor().getHitBox(), e -> e.equals(Player.instance()))) {
      if (Game.time().since(this.lastHit) > hitDelay) {
        ent.hit(damage, this.getAbility());
        lastHit = Game.time().now();
      }
    }

    Game.physics().move(this.getAbility().getExecutor(), this.angle, maxPixelsPerTick);

    super.update();
  }

  @Override
  protected void apply(final ICombatEntity entity) {
    this.angle = GeometricUtilities.calcRotationAngleInDegrees(this.getAbility().getExecutor().getCenter(), Player.instance().getCenter());

    super.apply(entity);
  }
}
