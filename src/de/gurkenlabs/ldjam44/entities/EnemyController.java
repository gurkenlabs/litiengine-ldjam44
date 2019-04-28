package de.gurkenlabs.ldjam44.entities;

import de.gurkenlabs.ldjam44.entities.Enemy.EnemyType;
import de.gurkenlabs.litiengine.Game;
import de.gurkenlabs.litiengine.physics.MovementController;
import de.gurkenlabs.litiengine.util.MathUtilities;
import de.gurkenlabs.litiengine.util.geom.GeometricUtilities;

public class EnemyController extends MovementController<Enemy> {
  private static final int CHARGE_DIST = 20;
  private static final int CHARGE_PREPARE_DURATION = 2000;

  private static final int STOMP_PREPARE_DURATION = 3000;

  public enum EnemyState {
    IDLE,
    CHASE,
    STRIKE,
    PREPARE_CHARGE,
    CHARGE,
    PREPARE_STOMP,
    STOMP,
  }

  private EnemyState state = EnemyState.IDLE;
  private long prepareStart;
  private long prepareStompStart;
  private boolean preparing;
  private boolean preparingStomp;

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
      if (this.getEntity().canCast()) {
        this.getEntity().setAngle(GeometricUtilities.calcRotationAngleInDegrees(this.getEntity().getCenter(), Player.instance().getCenter()));
        this.getEntity().getStrike().cast();
      }
      break;
    case CHASE:
      double angle = MathUtilities.randomInRange(-10, 10) + (int) GeometricUtilities.calcRotationAngleInDegrees(this.getEntity().getCenter(), Player.instance().getCenter());
      this.getEntity().setAngle(angle);
      Game.physics().move(this.getEntity(), this.getEntity().getAngle(), this.getEntity().getTickVelocity());
      break;
    case PREPARE_CHARGE:
      return;
    case CHARGE:
      if (this.getEntity().canCast()) {
        this.getEntity().getCharge().cast();
      }
      break;
    case PREPARE_STOMP:
      break;
    case STOMP:
      if (this.getEntity().canCast()) {
        this.getEntity().getStomp().cast();
      }
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

    if (!this.preparing
        && !this.preparingStomp
        && Player.instance().getCenter().distance(this.getEntity().getCenter()) < this.getEntity().getStrike().getAttributes().getImpact().getCurrentValue()
        && !Player.instance().isDead()) {
      this.state = EnemyState.STRIKE;
      return;
    }

    if (preparingStomp && this.prepareStompStart != 0 && Game.time().since(this.prepareStompStart) >= STOMP_PREPARE_DURATION) {
      this.preparingStomp = false;

      this.state = EnemyState.STOMP;
      return;
    }

    // STOMP
    if (!this.preparing
        && this.getEntity().getStomp().getRemainingCooldownInSeconds() == 0
        && this.getEntity().getType() == EnemyType.gold
        && Player.instance().getCenter().distance(this.getEntity().getCenter()) < this.getEntity().getStomp().getAttributes().getImpact().getCurrentValue()) {
      this.state = EnemyState.PREPARE_STOMP;

      if (!this.preparingStomp) {
        if (MathUtilities.probabilityIsTrue(0.5)) {
          this.prepareStompStart = Game.time().now();
          this.preparingStomp = true;

          String prepare = this.getEntity().getSpritePrefix() + "-prepare";
          if (GeometricUtilities.calcRotationAngleInDegrees(this.getEntity().getCollisionBoxCenter(), Player.instance().getCenter()) > 180) {
            prepare += "-left";
          }

          this.getEntity().getAnimationController().playAnimation(prepare);
        }

        this.getEntity().setScaling(true);
        this.getEntity().setWidth(17);
        this.getEntity().setHeight(21);
      }

      return;
    }

    if (preparing && this.prepareStart != 0 && Game.time().since(this.prepareStart) >= CHARGE_PREPARE_DURATION) {
      this.preparing = false;
      this.state = EnemyState.CHARGE;
      return;
    }

    if (!this.preparingStomp
        && this.getEntity().getCharge().getRemainingCooldownInSeconds() == 0
        && this.getEntity().getType() != EnemyType.leather
        && Player.instance().getCenter().distance(this.getEntity().getCenter()) > CHARGE_DIST) {
      this.state = EnemyState.PREPARE_CHARGE;

      if (!this.preparing) {
        this.prepareStart = Game.time().now();
        this.preparing = true;

        String prepare = this.getEntity().getSpritePrefix() + "-prepare";
        if (GeometricUtilities.calcRotationAngleInDegrees(this.getEntity().getCollisionBoxCenter(), Player.instance().getCenter()) > 180) {
          prepare += "-left";
        }

        this.getEntity().getAnimationController().playAnimation(prepare);
      }

      return;
    }

    this.state = EnemyState.CHASE;
  }

  public double getPreparation() {
    if (!this.preparing) {
      return 0;
    }

    return Game.time().since(this.prepareStart) / (double) CHARGE_PREPARE_DURATION;
  }

  public double getStompPreparation() {
    if (!this.preparingStomp) {
      return 0;
    }

    return Game.time().since(this.prepareStompStart) / (double) STOMP_PREPARE_DURATION;
  }
}
