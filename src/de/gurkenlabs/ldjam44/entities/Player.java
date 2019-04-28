package de.gurkenlabs.ldjam44.entities;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

import de.gurkenlabs.ldjam44.GameManager;
import de.gurkenlabs.ldjam44.abilities.JumpAbility;
import de.gurkenlabs.ldjam44.abilities.Strike;
import de.gurkenlabs.ldjam44.graphics.HitEmitter;
import de.gurkenlabs.litiengine.Game;
import de.gurkenlabs.litiengine.IUpdateable;
import de.gurkenlabs.litiengine.annotation.CollisionInfo;
import de.gurkenlabs.litiengine.annotation.CombatInfo;
import de.gurkenlabs.litiengine.annotation.EntityInfo;
import de.gurkenlabs.litiengine.annotation.MovementInfo;
import de.gurkenlabs.litiengine.entities.CombatEntityHitEvent;
import de.gurkenlabs.litiengine.entities.Creature;
import de.gurkenlabs.litiengine.entities.ICollisionEntity;
import de.gurkenlabs.litiengine.entities.ICombatEntity;
import de.gurkenlabs.litiengine.graphics.IRenderable;
import de.gurkenlabs.litiengine.graphics.OverlayPixelsImageEffect;
import de.gurkenlabs.litiengine.graphics.RenderType;
import de.gurkenlabs.litiengine.graphics.Spritesheet;
import de.gurkenlabs.litiengine.graphics.animation.Animation;
import de.gurkenlabs.litiengine.graphics.animation.IAnimationController;
import de.gurkenlabs.litiengine.graphics.emitters.AnimationEmitter;
import de.gurkenlabs.litiengine.graphics.emitters.Emitter;
import de.gurkenlabs.litiengine.input.KeyboardEntityController;
import de.gurkenlabs.litiengine.physics.IMovementController;
import de.gurkenlabs.litiengine.resources.Resources;
import de.gurkenlabs.litiengine.util.Imaging;
import de.gurkenlabs.litiengine.util.geom.GeometricUtilities;

@EntityInfo(width = 11, height = 20)
@MovementInfo(velocity = 30)
@CollisionInfo(collisionBoxWidth = 5, collisionBoxHeight = 8, collision = true)
@CombatInfo(hitpoints = 5, team = 1)
public class Player extends Creature implements IRenderable, IUpdateable {
  public enum PlayerState {
    CONTROLLABLE,
    LOCKED
  }

  private static Player instance;
  private final Strike strike;
  private final JumpAbility dash;

  private long lastWalkDust = 0;
  private PlayerState state = PlayerState.LOCKED;

  private Player() {
    super("monger");

    this.strike = new Strike(this);
    this.dash = new JumpAbility(this);
    this.setController(IMovementController.class, new KeyboardEntityController<>(this));
    this.getMovementController().onMoved(this::spawnWalkDust);
    this.getMovementController().onMovementCheck(e -> {
      return this.getState() == PlayerState.CONTROLLABLE;
    });

    this.addHitListener(e -> {
      spawnHitEmitter(e.getEntity(), e);
    });
    
    this.setMapId(100000);
  }

  private static void spawnHitEmitter(ICombatEntity entity, CombatEntityHitEvent args) {
    if (args.getDamage() <= 0) {
      return;
    }

    Game.world().camera().shake(1, 30, 250);

    Emitter emitter = new HitEmitter(entity, 10);
    Game.world().environment().add(emitter);

    args.getEntity().getAnimationController().add(new OverlayPixelsImageEffect(120, new Color(255, 255, 255, 200)));
    Game.loop().perform(130, () -> args.getEntity().getAnimationController().add(new OverlayPixelsImageEffect(120, new Color(255, 0, 0, 200))));
  }

  public static Player instance() {
    if (instance == null) {
      instance = new Player();
    }

    return instance;
  }

  @Override
  public boolean canCollideWith(final ICollisionEntity otherEntity) {
    if (this.dash.isActive()) {
      return !(otherEntity instanceof Creature);
    }

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
    if (Game.config().debug().isDebugEnabled()) {
      this.strike.render(g);
    }
  }

  public JumpAbility getDash() {
    return dash;
  }

  public boolean canTrigger() {
    return Game.world().environment().findCombatEntities(GeometricUtilities.extrude(this.getBoundingBox(), 2)).stream().anyMatch(x -> {
      if (x instanceof Enemy) {
        Enemy e = (Enemy) x;
        return !e.isEngaged() && !e.isEngaging();
      }
      return false;
    });
  }

  public boolean canTalkToGateKeeper() {
    Gatekeeper keeper = GameManager.getGateKeeper();
    if (keeper == null) {
      return false;
    }

    return keeper.getBoundingBox().intersects(GeometricUtilities.extrude(this.getBoundingBox(), 2));
  }

  private void initAnimationController() {
    IAnimationController controller = this.getAnimationController();
    Spritesheet jump = Resources.spritesheets().get("monger-jump");
    controller.add(new Animation(jump, false));

    final BufferedImage rightJump = Imaging.flipSpritesHorizontally(jump);
    Spritesheet rightJumpSprite = Resources.spritesheets().load(rightJump, "monger-jump-right", jump.getSpriteWidth(), jump.getSpriteHeight());
    controller.add(new Animation(rightJumpSprite, false));

    controller.setDefaultAnimation(controller.getAnimation("monger-idle"));
  }

  @Override
  protected void updateAnimationController() {
    super.updateAnimationController();
    this.initAnimationController();
  }

  public PlayerState getState() {
    return state;
  }

  public void setState(PlayerState state) {
    this.state = state;
  }

  @Override
  public void update() {
  }
}
