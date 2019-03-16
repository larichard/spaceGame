package cs2.game

import scalafx.scene.canvas.GraphicsContext
import scalafx.scene.image.Image
import cs2.util.Vec2
import scala.collection.mutable.Buffer

/** contains the control and logic to present a coordinated set of Enemy objects.
 *  For now, this class generates a "grid" of enemy objects centered near the 
 *  top of the screen.
 *  
 *  @param nRows - number of rows of enemy objects
 *  @param nCols - number of columns of enemy objects
 */
class EnemySwarm(private val nRows:Int, private val nCols:Int) extends ShootsBullets {
	
  //x and y parameters of swarm build
  var maxBuildY = 400
  var maxBuildX = 1000
  var minBuildY = 50
  var minBuildX = 25
  
  //images
  val bulletimg = new Image("file:bullet.png")
  val img = new Image("file:enemy.png")
  
  //build of swarm
  var enemies = Buffer[Enemy]()
  for(row <- 0 until nRows) {
    for(col <- 0 until nCols) {
      //!!! NEED TO ADD TO BUILD ELSEWHERE IF ENEMIES IN SWARM OVERLAP
      var initPos = new Vec2(minBuildX + (1000/nCols * col), minBuildY + (maxBuildY/nRows * row))
      var b = new Enemy(img, initPos, bulletimg)
      enemies += b
    }
  }
  
  /** method to display all Enemy objects contained within this EnemySwarm
	 * 
	 *  @param g - the GraphicsContext to draw into
	 *  @return none/Unit
	 */
  def display(g:GraphicsContext) {
    enemies.foreach(_.display(g))
  }
  
  /** overridden method of ShootsBullets. Creates a single, new bullet instance 
   *  originating from a random enemy in the swarm. (Not a bullet from every 
   *  object, just a single from a random enemy)
   *  
   *  @return Bullet - the newly created Bullet object fired from the swarm
   */
  //shoots bullet at random index of swarm
  def shoot():Bullet = {
    var random = new scala.util.Random
    var ind = random.nextInt(nRows * nCols)
    enemies(ind).shoot
  }
  
}