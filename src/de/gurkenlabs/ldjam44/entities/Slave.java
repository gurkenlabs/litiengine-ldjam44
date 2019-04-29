package de.gurkenlabs.ldjam44.entities;

import java.awt.geom.Point2D;

import de.gurkenlabs.ldjam44.GameManager;
import de.gurkenlabs.ldjam44.GameManager.GameState;
import de.gurkenlabs.litiengine.Game;
import de.gurkenlabs.litiengine.annotation.AnimationInfo;
import de.gurkenlabs.litiengine.annotation.CollisionInfo;
import de.gurkenlabs.litiengine.annotation.CombatInfo;
import de.gurkenlabs.litiengine.annotation.EntityInfo;
import de.gurkenlabs.litiengine.annotation.MovementInfo;
import de.gurkenlabs.litiengine.entities.ICollisionEntity;
import de.gurkenlabs.litiengine.resources.Resources;
import de.gurkenlabs.litiengine.sound.Sound;
import de.gurkenlabs.litiengine.util.ArrayUtilities;

@MovementInfo(velocity = 15)
@CombatInfo(hitpoints = 1, team = 2)
@CollisionInfo(collisionBoxWidth = 3f, collisionBoxHeight = 5f, collision = true)
@EntityInfo(width = 7, height = 13)
@AnimationInfo(spritePrefix = { "slave-centurio", "slave-monger" })
public class Slave extends Mob {
  private static final Sound[] deathSounds = new Sound[] { Resources.sounds().get("slave-death1.ogg"), Resources.sounds().get("slave-death2.ogg"), Resources.sounds().get("slave-death3.ogg"), Resources.sounds().get("slave-death4.ogg"), Resources.sounds().get("slave-death5.ogg"), Resources.sounds().get("slave-death6.ogg") };
  
  private String owner;

  public Slave() {
    this.addController(new SlaveController(this));
    this.addDeathListener(l -> {
      SlaveController.KILL_TICK = Game.time().now();
      Game.audio().playSound(ArrayUtilities.getRandom(deathSounds));
      
      Gatekeeper keeper = GameManager.getGateKeeper();
      if (keeper != null && GameManager.getAliveSlaveCount() < GameManager.getGateKeeper().getRequiredSlaves()) {
        GameManager.setState(GameState.SLAVES_DEAD);
      }
    });
  }

  public Slave(Point2D location) {
    this();
    this.setLocation(location);
  }

  @Override
  public boolean canCollideWith(final ICollisionEntity otherEntity) {
    return !(otherEntity instanceof Player) && !(otherEntity instanceof Slave) && !(otherEntity instanceof Enemy) && !(otherEntity instanceof DecorMob);
  }

  public Enemy getOwner() {
    if (this.owner == null) {
      return null;
    }

    return Game.world().environment().get(Enemy.class, this.owner);
  }

  public void setOwner(String owner) {
    this.owner = owner;
  }
}
