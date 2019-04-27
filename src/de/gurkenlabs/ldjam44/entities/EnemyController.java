package de.gurkenlabs.ldjam44.entities;

import de.gurkenlabs.litiengine.Game;
import de.gurkenlabs.litiengine.physics.MovementController;
import de.gurkenlabs.litiengine.util.MathUtilities;
import de.gurkenlabs.litiengine.util.geom.GeometricUtilities;

public class EnemyController extends MovementController<Enemy> {

  public EnemyController(Enemy mobileEntity) {
    super(mobileEntity);
  }

  @Override
  public void update() {
    super.update();

    if (!this.getEntity().isEngaged() || this.getEntity().isDead()) {
      return;
    }

    // hit him hard
    if (Player.instance().getCenter().distance(this.getEntity().getCenter()) < this.getEntity().getStrike().getAttributes().getImpact().getCurrentValue()) {
      this.getEntity().setAngle(GeometricUtilities.calcRotationAngleInDegrees(this.getEntity().getCenter(), Player.instance().getCenter()));
      this.getEntity().getStrike().cast();
    } else {
      double angle = MathUtilities.randomInRange(-10, 10) + (int) GeometricUtilities.calcRotationAngleInDegrees(this.getEntity().getCenter(), Player.instance().getCenter());
      this.getEntity().setAngle(angle);
      Game.physics().move(this.getEntity(), this.getEntity().getAngle(), this.getEntity().getTickVelocity());
    }
  }
}
