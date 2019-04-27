package de.gurkenlabs.ldjam44.entities;

import java.util.Random;

import de.gurkenlabs.litiengine.Game;
import de.gurkenlabs.litiengine.physics.MovementController;
import de.gurkenlabs.litiengine.util.MathUtilities;
import de.gurkenlabs.litiengine.util.geom.GeometricUtilities;

public class SlaveController extends MovementController<Slave> {
  public static long KILL_TICK = -1;
  private static final int ANGLE_CHANGE_MIN_DELAY = 1000;
  private static final int ANGLE_CHANGE_RANDOM_DELAY = 1000;
  private static final int FEARED = 3000;
  private static final int MAX_FEARED = 6000;
  private static final int MIN_RANGE = 15;
  private static final int RETURN_RANGE = 30;
  private static final int MAX_RANGE = 50;
  private static final Random RANDOM = new Random();
  private long lastAngleChange;
  private long nextAngleChange;
  private int angle;
  private boolean fearReaction;
  private int currentFearDuration = MathUtilities.randomInRange(FEARED, MAX_FEARED);
  private boolean feared;

  public SlaveController(Slave mobileEntity) {
    super(mobileEntity);
  }

  @Override
  public void update() {
    super.update();

    this.walkAroundLikeMFs();
  }

  private void walkAroundLikeMFs() {

    // WALK AROUND LIKE MOTHERFUCKERS
    float pixelsPerTick = this.getEntity().getTickVelocity();
    final long currentTick = Game.time().now();
    final long timeSinceLastAngleChange = Game.time().since(this.lastAngleChange);
    final double currentDist = this.getEntity().getLocation().distance(Player.instance().getLocation());

    this.feared = KILL_TICK != -1 && Game.time().since(KILL_TICK) < currentFearDuration;

    if (feared && !fearReaction || this.angle == 0 || timeSinceLastAngleChange > this.nextAngleChange || currentDist < MIN_RANGE && timeSinceLastAngleChange > 500) {
      this.lastAngleChange = currentTick;
      this.calculateNextAngleChange();
      if (feared) {
        if (!fearReaction) {
          this.angle = MathUtilities.randomInRange(-20, 20) + 360 - (int) GeometricUtilities.calcRotationAngleInDegrees(this.getEntity().getCenter(), Player.instance().getCenter());
          this.getEntity().setVelocity(15);
          this.fearReaction = true;
          this.currentFearDuration = MathUtilities.randomInRange(FEARED, MAX_FEARED);
        }
      } else if (currentDist > RETURN_RANGE) {
        this.angle = MathUtilities.randomInRange(-10, 10) + (int) GeometricUtilities.calcRotationAngleInDegrees(this.getEntity().getCenter(), Player.instance().getCenter());
        this.getEntity().setVelocity(currentDist > MAX_RANGE ? 20 : 12);
      } else if (currentDist < MIN_RANGE) {
        this.angle = MathUtilities.randomInRange(-10, 10) + 360 - (int) GeometricUtilities.calcRotationAngleInDegrees(this.getEntity().getCenter(), Player.instance().getCenter());
        this.getEntity().setVelocity(8);
      } else {
        this.angle = RANDOM.nextInt(360);
        this.getEntity().setVelocity(8);
      }
    }

    if (!feared) {
      this.fearReaction = false;
    }

    this.getEntity().setAngle(this.angle);
    Game.physics().move(this.getEntity(), this.getEntity().getAngle(), pixelsPerTick);
  }

  private void calculateNextAngleChange() {
    this.nextAngleChange = RANDOM.nextInt(ANGLE_CHANGE_RANDOM_DELAY) + ANGLE_CHANGE_MIN_DELAY;
  }
}
