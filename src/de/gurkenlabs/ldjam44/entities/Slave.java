package de.gurkenlabs.ldjam44.entities;

import de.gurkenlabs.litiengine.Game;
import de.gurkenlabs.litiengine.annotation.CombatInfo;
import de.gurkenlabs.litiengine.annotation.MovementInfo;
import de.gurkenlabs.litiengine.entities.Creature;
import de.gurkenlabs.litiengine.entities.ICollisionEntity;

@MovementInfo(velocity = 15)
@CombatInfo(hitpoints = 1, team = 2)
public class Slave extends Creature {

  public Slave() {
    this.addController(new SlaveController(this));
    this.addDeathListener(l -> {
      SlaveController.KILL_TICK = Game.time().now();
    });
  }

  @Override
  public boolean canCollideWith(final ICollisionEntity otherEntity) {
    return !(otherEntity instanceof Player) && !(otherEntity instanceof Slave);
  }
}
