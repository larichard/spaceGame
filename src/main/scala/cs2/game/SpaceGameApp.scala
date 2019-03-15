package cs2.game

import scalafx.application.JFXApp
import scalafx.scene.Scene
import scalafx.scene.canvas.Canvas
import scalafx.scene.image.Image
import cs2.util.Vec2
import scalafx.animation.AnimationTimer
import scalafx.scene.input.KeyEvent
import scalafx.scene.input.KeyCode
import scalafx.Includes._
import scalafx.scene.paint.Color
import scala.collection.mutable.Buffer


/** main object that initiates the execution of the game, including construction
 *  of the window.
 *  Will create the stage, scene, and canvas to draw upon. Will likely contain or
 *  refer to an AnimationTimer to control the flow of the game.
 */
object SpaceGameApp extends JFXApp {
  
  val img = new Image("file:player2.png")
  val bulletimg = new Image("file:bullet.png")
  var lefttouch = false
  var righttouch = false
  var uptouch = false
  var downtouch = false
  var bullets = Buffer[Bullet]()
  var player = new Player(img, new Vec2(470,1000), bulletimg)
  var swarm = new EnemySwarm(4, 10)

  stage = new JFXApp.PrimaryStage {
    title = "SPACE"
    scene = new Scene(1000, 1080) {
      val canvas = new Canvas(1000, 1080)
      val g = canvas.graphicsContext2D
      content = canvas
      g.fill = Color.Black
      //g.fillRect(0,0, 800,600)
      
      onKeyPressed = (e:KeyEvent) => {
        if(e.code == KeyCode.A) {
          lefttouch = true
        }
        if(e.code == KeyCode.D) {
          righttouch = true
        }
        if(e.code == KeyCode.W) {
          uptouch = true
        }
        if(e.code == KeyCode.S) {
          downtouch = true
        }
        if(e.code == KeyCode.Space) {
          bullets += player.shoot
          var f = player.speed
          player = new Player(img, new Vec2(player.showPos.x, player.showPos.y), bulletimg)
        }
      }
      
      onKeyReleased = (e:KeyEvent) => {
        if(e.code == KeyCode.A) {
          lefttouch = false
        }
        if(e.code == KeyCode.D) {
          righttouch = false
        }
        if(e.code == KeyCode.W) {
          uptouch = false
        }
        if(e.code == KeyCode.S) {
          downtouch = false
        }
      }
            
      var lastTime = 0L
      val timer = AnimationTimer(time => {
        if (lastTime == 0) lastTime = time
        else {
          val interval = (time - lastTime) / 1e9
          lastTime = time
          
          g.fillRect(0,0, 1000,1080)
          player.display(g)
          swarm.display(g)
          
          val times = math.random()
          if (times < 0.05) {
          bullets += swarm.shoot()
          }
          swarm = new EnemySwarm(4,10)
          
          bullets.foreach(_.display(g))
          bullets.foreach(_.timeStep)
          
          if (lefttouch) {
            player.moveLeft()
            printf(player.showPos.toString + "\n")
            player.display(g)
          }
          if (righttouch) {
            player.moveRight()
            printf(player.showPos.toString + "\n")
            player.display(g)
          }
          if (uptouch) {
            player.moveUp()
            printf(player.showPos.toString + "\n")
            player.display(g)
          }
          if (downtouch) {
            player.moveDown()
            printf(player.showPos.toString + "\n")
            player.display(g)
          }
        }
      })
      timer.start
      canvas.requestFocus() 
    }
  }
}