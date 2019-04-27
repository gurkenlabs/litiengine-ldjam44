package de.gurkenlabs.ldjam44.entities;

import de.gurkenlabs.litiengine.physics.MovementController;

public class EnemyController extends MovementController<Slave> {

  public EnemyController(Slave mobileEntity) {
    super(mobileEntity);
  }

}
