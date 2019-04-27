package de.gurkenlabs.ldjam44.entities;

import java.awt.Color;
import java.awt.geom.Point2D;

import de.gurkenlabs.litiengine.Game;
import de.gurkenlabs.litiengine.annotation.CollisionInfo;
import de.gurkenlabs.litiengine.annotation.CombatInfo;
import de.gurkenlabs.litiengine.annotation.EntityInfo;
import de.gurkenlabs.litiengine.annotation.MovementInfo;
import de.gurkenlabs.litiengine.entities.ICollisionEntity;
import de.gurkenlabs.litiengine.environment.Environment;
import de.gurkenlabs.litiengine.graphics.CreatureShadowImageEffect;

@MovementInfo(velocity = 15)
@CombatInfo(hitpoints = 1, team = 2)
@CollisionInfo(collisionBoxWidth = 3f, collisionBoxHeight = 5f, collision = true)
@EntityInfo(width = 7, height = 13)
public class Slave extends Mob {
  private int owner;

  public Slave() {
    this.addController(new SlaveController(this));
    this.addDeathListener(l -> {
      SlaveController.KILL_TICK = Game.time().now();
    });
  }

  public Slave(Point2D location) {
    this();
    this.setLocation(location);
  }

  @Override
  public boolean canCollideWith(final ICollisionEntity otherEntity) {
    return !(otherEntity instanceof Player) && !(otherEntity instanceof Slave) && !(otherEntity instanceof Enemy);
  }

  @Override
  public void loaded(Environment environment) {
    super.loaded(environment);

    this.getAnimationController().add(new CreatureShadowImageEffect(this, new Color(0, 0, 0, 150)));
  }

  public Enemy getOwner() {
    if (this.owner == 0) {
      return null;
    }

    return Game.world().environment().get(Enemy.class, this.owner);
  }

  public void setOwner(int owner) {
    this.owner = owner;
  }
}
