package cs2.game

import scala.collection.mutable.Buffer
import scalafx.scene.canvas.GraphicsContext

class SpecialEnemySwarm(private val nRows:Int, private val nCols:Int) {
  var enemies = Buffer[SpecialEnemy]()
  
  def display(g:GraphicsContext) {
    enemies.foreach(_.display(g))
  }
  
  def shoot():Bullet = {
    enemies(0).shoot
  }

}