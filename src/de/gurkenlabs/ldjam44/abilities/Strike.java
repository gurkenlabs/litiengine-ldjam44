package de.gurkenlabs.ldjam44.abilities;

import java.awt.geom.Point2D;
import java.util.stream.Collectors;

import de.gurkenlabs.ldjam44.entities.Enemy;
import de.gurkenlabs.ldjam44.entities.Player;
import de.gurkenlabs.ldjam44.graphics.StrikeEmitter;
import de.gurkenlabs.litiengine.Direction;
import de.gurkenlabs.litiengine.Game;
import de.gurkenlabs.litiengine.abilities.OffensiveAbility;
import de.gurkenlabs.litiengine.annotation.AbilityInfo;
import de.gurkenlabs.litiengine.entities.Creature;
import de.gurkenlabs.litiengine.graphics.RenderType;
import de.gurkenlabs.litiengine.graphics.emitters.SpritesheetEmitter;
import de.gurkenlabs.litiengine.resources.Resources;
import de.gurkenlabs.litiengine.util.geom.GeometricUtilities;

@AbilityInfo(name = "Strike", cooldown = 700, range = 0, impact = 13, impactAngle = 360, value = 1, duration = 400, multiTarget = true)
public class Strike extends OffensiveAbility {

  public Strike(Creature executor) {
    super(executor);

    this.addEffect(new HitEffect(this));

    if (executor instanceof Player) {
      this.addEffect(new ScreenShakeEffect(this, 1, 100));
    }

    this.onCast(e -> {

      RenderType renderType = RenderType.OVERLAY;
      String sprite = "hit";
      double x = this.getExecutor().getCenter().getX();
      double y = this.getExecutor().getCenter().getY();

      // face toward the closest enemy
      Enemy closestInRange = null;
      double dist = 0;
      for (Enemy enemy : Game.world().environment().getByType(Enemy.class).stream().filter(en -> GeometricUtilities.shapeIntersects(en.getHitBox(), this.calculateImpactArea())).collect(Collectors.toList())) {
        double newDist = enemy.getLocation().distance(this.getOrigin());
        if (closestInRange == null || newDist < dist) {
          dist = newDist;
          closestInRange = enemy;
        }
      }

      double angle = Player.instance().getAngle();
      if (closestInRange != null) {
        angle = GeometricUtilities.calcRotationAngleInDegrees(this.getOrigin(), closestInRange.getCenter());
      }

      switch (Direction.fromAngle(angle)) {
      case RIGHT:
        x -= 2;
        sprite = "hit-right";
        break;
      case LEFT:
        x -= 10;
        sprite = "hit-left";
        break;
      case UP:
        x -= 8;
        y -= 3;
        sprite = "hit-top";
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
}
