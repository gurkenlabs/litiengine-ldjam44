package de.gurkenlabs.ldjam44.abilities;

import de.gurkenlabs.ldjam44.entities.Player;
import de.gurkenlabs.ldjam44.graphics.LandEmitter;
import de.gurkenlabs.litiengine.Game;
import de.gurkenlabs.litiengine.abilities.Ability;
import de.gurkenlabs.litiengine.abilities.effects.Effect;
import de.gurkenlabs.litiengine.abilities.effects.EffectApplication;
import de.gurkenlabs.litiengine.abilities.effects.EffectTarget;
import de.gurkenlabs.litiengine.entities.ICombatEntity;
import de.gurkenlabs.litiengine.resources.Resources;

public class JumpEffect extends Effect {
  private double angle;
  private double factor;

  public JumpEffect(final Ability ability) {
    super(ability, EffectTarget.EXECUTINGENTITY);
  }

  @Override
  public void update() {
    final long deltaTime = Game.loop().getDeltaTime();
    final double maxPixelsPerTick = this.getAbility().getAttributes().getValue().getCurrentValue() / 1000.0 * Math.min(deltaTime, 50);
    Game.physics().move(Player.instance(), this.angle, maxPixelsPerTick);

    if (this.factor != 0) {
      double newWidth = Player.instance().getWidth() * this.factor;
      double newHeight = Player.instance().getHeight() * this.factor;

      double diffX = Player.instance().getWidth() - newWidth;
      double diffY = Player.instance().getHeight() - newHeight;

      Player.instance().setX(Player.instance().getX() + diffX / 2.0);
      Player.instance().setY(Player.instance().getY() + diffY / 2.0);

      Player.instance().setWidth(newWidth);
      Player.instance().setHeight(newHeight);
      Player.instance().setCollisionBoxWidth(Player.instance().getCollisionBoxWidth() * this.factor);
      Player.instance().setCollisionBoxHeight(Player.instance().getCollisionBoxHeight() * this.factor);

    }
    super.update();
  }

  @Override
  protected void apply(final ICombatEntity entity) {
    this.angle = Player.instance().getAngle();
    Player.instance().setWidth(11);
    Player.instance().setHeight(20);
    Player.instance().setCollisionBoxWidth(5);
    Player.instance().setCollisionBoxHeight(8);
    this.factor = 1.025;
    Game.loop().perform(this.getAbility().getAttributes().getDuration().getCurrentValue() / 2, () -> {
      this.factor = 0.975;
    });
    Player.instance().setScaling(true);

    String jumpAnimation = "monger-jump";
    if (this.angle < 180) {
      jumpAnimation = "monger-jump-right";
    }

    Player.instance().getAnimationController().playAnimation(jumpAnimation);

    Game.audio().playSound(Resources.sounds().get("jump.ogg"));
    super.apply(entity);
  }

  @Override
  protected void cease(EffectApplication appliance) {
    Player.instance().setWidth(11);
    Player.instance().setHeight(20);
    Player.instance().setCollisionBoxWidth(5);
    Player.instance().setCollisionBoxHeight(8);
    Player.instance().setScaling(false);

    Game.world().environment().add(new LandEmitter(Player.instance()));
    super.cease(appliance);
  }
}
