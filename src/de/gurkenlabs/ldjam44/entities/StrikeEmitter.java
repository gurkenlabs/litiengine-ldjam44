package de.gurkenlabs.ldjam44.entities;

import java.awt.geom.Point2D;

import de.gurkenlabs.litiengine.annotation.EmitterInfo;
import de.gurkenlabs.litiengine.graphics.Spritesheet;
import de.gurkenlabs.litiengine.graphics.animation.Animation;
import de.gurkenlabs.litiengine.graphics.emitters.AnimationEmitter;
import de.gurkenlabs.litiengine.graphics.emitters.SpritesheetEmitter;
import de.gurkenlabs.litiengine.graphics.emitters.particles.Particle;

@EmitterInfo(particleMinTTL = 100, particleMaxTTL = 100, emitterTTL = 150, maxParticles = 1)
public class StrikeEmitter extends AnimationEmitter {

  public StrikeEmitter(Spritesheet spriteSheet, Point2D origin) {
    super(spriteSheet, origin);
  }
}
