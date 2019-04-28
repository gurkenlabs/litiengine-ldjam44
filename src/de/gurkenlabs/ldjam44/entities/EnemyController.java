package de.gurkenlabs.ldjam44.entities;

import de.gurkenlabs.ldjam44.entities.Enemy.EnemyType;
import de.gurkenlabs.litiengine.Game;
import de.gurkenlabs.litiengine.physics.MovementController;
import de.gurkenlabs.litiengine.util.MathUtilities;
import de.gurkenlabs.litiengine.util.geom.GeometricUtilities;

public class EnemyController extends MovementController<Enemy> {
  private static final int CHARGE_DIST = 20;
  private static final int CHARGE_PREPARE_DURATION = 2000;

  public enum EnemyState {
    IDLE,
    CHASE,
    STRIKE,
    PREPARE_CHARGE,
    CHARGE,
  }

  private EnemyState state = EnemyState.IDLE;
  private long prepareStart;
  private boolean preparing;

  public EnemyController(Enemy mobileEntity) {
    super(mobileEntity);
  }

  @Override
  public void update() {
    super.update();

    this.evaluateState();

    if (!this.getEntity().isEngaged() || this.getEntity().isDead()) {
      return;
    }

    switch (this.state) {
    case STRIKE:
      this.getEntity().setAngle(GeometricUtilities.calcRotationAngleInDegrees(this.getEntity().getCenter(), Player.instance().getCenter()));
      this.getEntity().getStrike().cast();
      break;
    case CHASE:
      double angle = MathUtilities.randomInRange(-10, 10) + (int) GeometricUtilities.calcRotationAngleInDegrees(this.getEntity().getCenter(), Player.instance().getCenter());
      this.getEntity().setAngle(angle);
      Game.physics().move(this.getEntity(), this.getEntity().getAngle(), this.getEntity().getTickVelocity());
      break;
    case PREPARE_CHARGE:

      break;
    case CHARGE:
      this.getEntity().getCharge().cast();
      break;
    case IDLE:
    default:
      break;
    }
  }

  private void evaluateState() {
    if (!this.getEntity().isEngaged() || this.getEntity().isDead()) {
      this.state = EnemyState.IDLE;
      return;
    }

    if (Player.instance().getCenter().distance(this.getEntity().getCenter()) < this.getEntity().getStrike().getAttributes().getImpact().getCurrentValue()) {
      this.state = EnemyState.STRIKE;
      return;
    }

    if (this.getEntity().getCharge().getRemainingCooldownInSeconds() == 0 && this.getEntity().getType() != EnemyType.leather && Player.instance().getCenter().distance(this.getEntity().getCenter()) > CHARGE_DIST) {
      this.state = EnemyState.PREPARE_CHARGE;
      
      if(!this.preparing) {
        this.prepareStart = Game.time().now();
        this.preparing = true;
      }
      
      if(Game.time().since(this.prepareStart) >= CHARGE_PREPARE_DURATION) {
        this.preparing = false;
        this.state = EnemyState.CHARGE;
      }
      
      return;
    }

    this.state = EnemyState.CHASE;
  }
}
