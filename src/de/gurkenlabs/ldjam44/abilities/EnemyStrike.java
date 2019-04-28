package de.gurkenlabs.ldjam44.abilities;

import java.awt.geom.Point2D;
import java.util.stream.Collectors;

import de.gurkenlabs.ldjam44.entities.Enemy;
import de.gurkenlabs.ldjam44.entities.Player;
import de.gurkenlabs.ldjam44.graphics.StrikeEmitter;
import de.gurkenlabs.litiengine.Direction;
import de.gurkenlabs.litiengine.Game;
import de.gurkenlabs.litiengine.abilities.AbilityOrigin;
import de.gurkenlabs.litiengine.abilities.OffensiveAbility;
import de.gurkenlabs.litiengine.annotation.AbilityInfo;
import de.gurkenlabs.litiengine.entities.Creature;
import de.gurkenlabs.litiengine.graphics.RenderType;
import de.gurkenlabs.litiengine.graphics.emitters.SpritesheetEmitter;
import de.gurkenlabs.litiengine.resources.Resources;
import de.gurkenlabs.litiengine.util.geom.GeometricUtilities;

@AbilityInfo(name = "Strike", cooldown = 1500, range = 0, impact = 10, impactAngle = 360, value = 1, duration = 400, multiTarget = true, origin = AbilityOrigin.DIMENSION_CENTER)
public class EnemyStrike extends OffensiveAbility {

  private final HitEffect hitEffect;

  public EnemyStrike(Creature executor) {
    super(executor);

    this.hitEffect = new HitEffect(this);
    this.addEffect(this.hitEffect);

    if (executor instanceof Player) {
      this.addEffect(new ScreenShakeEffect(this, 1, 100));
    }

    this.onCast(e -> {

      RenderType renderType = RenderType.OVERLAY;
      String sprite = "hit-red";
      double x = this.getExecutor().getCenter().getX();
      double y = this.getExecutor().getCenter().getY();

      double angle = GeometricUtilities.calcRotationAngleInDegrees(this.getOrigin(), Player.instance().getCenter());

      switch (Direction.fromAngle(angle)) {
      case RIGHT:
        x -= 2;
        sprite = "hit-right-red";
        break;
      case LEFT:
        x -= 10;
        sprite = "hit-left-red";
        break;
      case UP:
        x -= 8;
        y -= 3;
        sprite = "hit-top-red";
        renderType = RenderType.SURFACE;
        break;
      default:
        x -= 8;
        y += 7;
        break;
      }

      SpritesheetEmitter dashEmitter = new StrikeEmitter(Resources.spritesheets().get(sprite), new Point2D.Double(x, y));
      dashEmitter.setRenderType(renderType);
      Game.world().environment().add(dashEmitter);
    });
  }

  public HitEffect getHitEffect() {
    return this.hitEffect;
  }
}
