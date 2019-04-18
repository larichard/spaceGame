package cs2.game

import cs2.util.Vec2
import scalafx.scene.image.Image

class SpecialEnemy(pic:Image, initPos:Vec2, private val bulletPic:Image) 
                  extends Sprite(pic, initPos) with ShootsBullets {
  
  var showPos = initPos
  
  def shoot():Bullet = {
    new Bullet(bulletPic, new Vec2(initPos.x+66, initPos.y+100), new Vec2(0.0, 8.0))
    //new Bullet(bulletPic, new Vec2(initPos.x+100, initPos.y+50), new Vec2(0.0, 8.0))
  }
}