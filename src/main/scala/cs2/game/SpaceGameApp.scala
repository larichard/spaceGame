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
  
  stage = new JFXApp.PrimaryStage {
    title = "SPACE"
    val img = new Image("file:player2.png")
    scene = new Scene(800, 600) {
      val canvas = new Canvas(800, 600)
      val g = canvas.graphicsContext2D
      content = canvas
      g.fill = Color.Black
      g.fillRect(0,0, 800,600)
      
      var bullets = Buffer[Bullet]()
      
      var player = new Player(img, new Vec2(384.5, 500), img)
      player.display(g)
            
      onKeyPressed = (e:KeyEvent) => {
        if(e.code == KeyCode.A) {
          player.moveLeft()
          printf(player.show.toString + "\n"  )
          player.display(g)
        }
        if(e.code == KeyCode.D) {
          player.moveRight()
          printf(player.show.toString + "\n")
          player.display(g)
        }
        if(e.code == KeyCode.Space) {
          player.shoot()
          bullets += new Bullet(img, new Vec2(384.5, 500), new Vec2())
          
        }
      }
      
      var swarm = new EnemySwarm(2, 10)
      swarm.display(g)
      
      //var bullets = Buffer[Bullet]()
      
      
      var prevT:Long = 0L
      val timer = AnimationTimer(t => {
        if(t - prevT > 1e9/60) {
          prevT = t
          
          for (sys <- bullets) {
            sys.display(g)
            sys.timeStep()
          }
        }
      })
      timer.start
    }
  }
}