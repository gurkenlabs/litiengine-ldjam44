package de.gurkenlabs.ldjam44.entities;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;

import de.gurkenlabs.litiengine.Game;
import de.gurkenlabs.litiengine.annotation.CollisionInfo;
import de.gurkenlabs.litiengine.annotation.CombatInfo;
import de.gurkenlabs.litiengine.annotation.EntityInfo;
import de.gurkenlabs.litiengine.annotation.MovementInfo;
import de.gurkenlabs.litiengine.entities.Creature;
import de.gurkenlabs.litiengine.entities.ICollisionEntity;
import de.gurkenlabs.litiengine.graphics.CreatureShadowImageEffect;
import de.gurkenlabs.litiengine.graphics.IRenderable;
import de.gurkenlabs.litiengine.graphics.RenderType;
import de.gurkenlabs.litiengine.graphics.Spritesheet;
import de.gurkenlabs.litiengine.graphics.emitters.AnimationEmitter;
import de.gurkenlabs.litiengine.input.KeyboardEntityController;
import de.gurkenlabs.litiengine.physics.IMovementController;
import de.gurkenlabs.litiengine.resources.Resources;

@EntityInfo(width = 11, height = 20)
@MovementInfo(velocity = 30)
@CollisionInfo(collisionBoxWidth = 5, collisionBoxHeight = 8, collision = true)
@CombatInfo(hitpoints = 5, team = 1)
public class Player extends Creature implements IRenderable {
  private static Player instance;

  private long lastWalkDust = 0;
  private final Strike strike;
  private final DashAbility dash;

  private Player() {
    super("monger");

    this.strike = new Strike(this);
    this.dash = new DashAbility(this);
    this.setController(IMovementController.class, new KeyboardEntityController<>(this));
    this.getMovementController().onMoved(this::spawnWalkDust);

    this.getAnimationController().add(new CreatureShadowImageEffect(this, new Color(0, 0, 0, 150)));
  }

  public static Player instance() {
    if (instance == null) {
      instance = new Player();
    }

    return instance;
  }

  @Override
  public boolean canCollideWith(final ICollisionEntity otherEntity) {
    return !(otherEntity instanceof Slave);
  }

  private void spawnWalkDust(Point2D delta) {
    final int STEP_DELAY = 360;
    if (delta.getX() == 0 && delta.getY() == 0 || Game.world().environment() == null) {
      return;
    }
    Spritesheet walkDustSprite = Resources.spritesheets().get("walk-dust");
    if (Game.time().since(lastWalkDust) < STEP_DELAY) {
      return;
    }

    this.lastWalkDust = Game.loop().getTicks();

    Point2D walkLocation = new Point2D.Double(this.getCollisionBoxCenter().getX() - walkDustSprite.getSpriteWidth() / 2.0, this.getCollisionBoxCenter().getY() - walkDustSprite.getSpriteHeight() / 2.0);
    AnimationEmitter walkDust = new AnimationEmitter(walkDustSprite, walkLocation);
    walkDust.setRenderType(RenderType.NORMAL);
    Game.world().environment().add(walkDust);
  }

  public Strike getStrike() {
    return strike;
  }

  @Override
  public void render(Graphics2D g) {
    this.strike.render(g);
  }

  public DashAbility getDash() {
    return dash;
  }
}
