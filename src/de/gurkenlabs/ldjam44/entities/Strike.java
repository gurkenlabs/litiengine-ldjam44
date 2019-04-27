package de.gurkenlabs.ldjam44.entities;

import de.gurkenlabs.litiengine.Game;

import java.awt.geom.Point2D;

import de.gurkenlabs.litiengine.Direction;
import de.gurkenlabs.litiengine.abilities.OffensiveAbility;
import de.gurkenlabs.litiengine.annotation.AbilityInfo;
import de.gurkenlabs.litiengine.entities.Creature;
import de.gurkenlabs.litiengine.graphics.RenderType;
import de.gurkenlabs.litiengine.graphics.emitters.SpritesheetEmitter;
import de.gurkenlabs.litiengine.resources.Resources;

@AbilityInfo(name = "Strike", cooldown = 700, range = 0, impact = 13, impactAngle = 360, value = 1, duration = 400, multiTarget = true)
public class Strike extends OffensiveAbility {

  protected Strike(Creature executor) {
    super(executor);

    this.addEffect(new HitEffect(this));
    this.onCast(e -> {

      RenderType renderType = RenderType.OVERLAY;
      String sprite = "hit";
      double x = this.getExecutor().getCenter().getX();
      double y = this.getExecutor().getCenter().getY();
      switch (Player.instance().getFacingDirection()) {
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
