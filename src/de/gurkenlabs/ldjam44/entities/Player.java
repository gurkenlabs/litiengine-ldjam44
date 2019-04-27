package de.gurkenlabs.ldjam44.entities;

import de.gurkenlabs.litiengine.annotation.CollisionInfo;
import de.gurkenlabs.litiengine.annotation.EntityInfo;
import de.gurkenlabs.litiengine.annotation.MovementInfo;
import de.gurkenlabs.litiengine.entities.Creature;
import de.gurkenlabs.litiengine.entities.ICollisionEntity;
import de.gurkenlabs.litiengine.input.KeyboardEntityController;

@EntityInfo(width = 11, height = 20)
@MovementInfo(velocity = 30)
@CollisionInfo(collisionBoxWidth = 5, collisionBoxHeight = 8, collision = true)
public class Player extends Creature {
  private static Player instance;
  
  private Player() {
    super("monger");
    
    this.addController(new KeyboardEntityController<>(this));
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

}
