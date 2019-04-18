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
import scala.concurrent.duration._
import java.util.Calendar

/** main object that initiates the execution of the game, including construction
 *  of the window.
 *  Will create the stage, scene, and canvas to draw upon. Will likely contain or
 *  refer to an AnimationTimer to control the flow of the game.
 */
object SpaceGameApp extends JFXApp {
  
  //images
  val special_enemy1 = new Image("file:special_enemy1.png")
  val special_enemy2 = new Image("file:special_enemy2.png")
  val special_enemy3 = new Image("file:special_enemy3.png")

  val space = new Image("file:space.jpg")
  val asteroidImg = new Image("file:asteroid.png")
  val star = new Image("file:star.png")
  
  val img = new Image("file:player2.png")
  val playerdamaged = new Image("file:player2damaged.png")
  val playerright = new Image("file:playerright.png")
  val playerleft = new Image("file:playerleft.png")
  
  val bulletimg = new Image("file:bullet.png")
  val backgroundimg_jk = new Image("file:background_jk.png")
  val endscreenimg2 = new Image("file:endscreen2.png")
  val explosionImg = new Image("file:explosion_64x64.png")
  
  //background variables
  val maxAst = 3
  var currAst = 0
  val maxStar = 6
  var currStar = 0
  
  //player bullet variables
  val shotMax = 3
  var shotAlready = 0
  var shotCooldown = 1.0
  
  //specialEnemy lives
  var numOfSpecial = 0
  var specialHit = 0
    
  //tracks score, player lives, # of waves cleared
  var damaged = false
  var playerLives = 3
  var playerHitPerLife = 2
  var score = 0
  var wavesCleared = 1
  
  //rows and columns of swarm
  var swarmRows = 3
  var swarmCols = 10
  var waveNum = wavesCleared - 1
  
  //swarm movement variables
  var moveLength = 1.0
  var hit = false
  
  //tracks if game is over
  var gameOver = false
  
  //tracks key inputs of player
  var lefttouch = false
  var righttouch = false
  var uptouch = false
  var downtouch = false
  var spacetouch = false
  val playerspeed = 8
  
  //positions at beginning of game
  var spaceBack = new Background(space, new Vec2(0,-380))
  var spaceBack2 = new Background(space, new Vec2(0,-380 - 1280))
  var asteroidBackground = Buffer[Background]()
  var starBackground = Buffer[Background]()
  
  var enemybullets = Buffer[Bullet]()
  var playerbullets = Buffer[Bullet]()
  var player = new Player(img, new Vec2(470,820), bulletimg)
  var swarm = new EnemySwarm(swarmRows, swarmCols)
  var specialSwarm = new SpecialEnemySwarm(0,0)
  
  stage = new JFXApp.PrimaryStage {
    title = "SPACE"
    scene = new Scene(1000, 900) {
      val canvas = new Canvas(1000, 900)
      val g = canvas.graphicsContext2D
      content = canvas
      
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
          //limits fire rate of player
          if(shotCooldown > 0 && shotAlready < shotMax) {
            playerbullets += player.shoot
            if(!damaged) {
              player = new Player(img, new Vec2(player.showPos.x, player.showPos.y), bulletimg)
            }
            if(damaged) {
              player = new Player(playerdamaged, new Vec2(player.showPos.x, player.showPos.y), bulletimg)
            }
            shotAlready += 1
          }
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
      val timer:AnimationTimer = AnimationTimer(time => {
        if (lastTime == 0) lastTime = time
        else {
          val interval = (time - lastTime) / 1e9
          lastTime = time
                    
          //fill background
          //g.fill = Color.Black
          //g.fillRect(0,0, 1000,900)
                              
          //display objects
          spaceBack.display(g)
          spaceBack2.display(g)
          starBackground.foreach(_.display(g))
          asteroidBackground.foreach(_.display(g))
                    
          player.display(g)
          swarm.display(g)
          enemybullets.foreach(_.display(g))
          playerbullets.foreach(_.display(g))
          specialSwarm.display(g)
          
          //display UI elements
          g.fill = Color.White
          g.fillText("LIVES: " + playerLives.toString, 10, 20)
          g.fillText("SCORE: " + score.toString, 10, 40)
          g.fillText("WAVE: " + wavesCleared.toString, 940, 20, 60)

          //give bullets velocity
          enemybullets.foreach(_.timeStep)
          playerbullets.foreach(_.timeStep)
          
          //background movement
          spaceBack.move(new Vec2(0, 0.5))
          spaceBack2.move(new Vec2(0, 0.5))
          if(spaceBack.showPos.y > 1280) {
            spaceBack.showPos.y = -1280
          }
          if(spaceBack2.showPos.y > 1280) {
            spaceBack2.showPos.y = -1280
          }
          
          var astRandom = math.random
          var asteroidSpawnX = math.random * 1000
          if(currAst < maxAst && astRandom <= 0.002) {
            asteroidBackground += new Background(asteroidImg, new Vec2(asteroidSpawnX, -300))
            currAst += 1
          }        
          asteroidBackground.foreach(_.move(new Vec2(0, 2)))
          for(j <- 0 until asteroidBackground.length) {
            if(asteroidBackground(j).showPos.y > 1400) {
              asteroidBackground -= asteroidBackground(j)
              currAst -= 1
            }
          }
          
          var starRandom = math.random
          var starSpawnX = math.random * 1000
          if(currStar < maxStar && starRandom < 0.004) {
            starBackground += new Background(star, new Vec2(starSpawnX, -50))
            currStar += 1
          }
          starBackground.foreach(_.move(new Vec2(0, 4)))
          for(j <- 0 until starBackground.length) {
            if(starBackground(j).showPos.y > 950) {
            starBackground -= starBackground(j)
            currStar -= 1
            }
          }
          
          //non-trivial movement for enemy swarm
          for(i <- 0 until swarm.enemies.length) {
            /*
            if(swarm.enemies(i).showPos.y > 700 && swarm.enemies(i).showPos.y < 960) {
              swarm.enemies.map(_.move(new Vec2(0, 20)))
            } else {
            */
              val enemydir = math.random()
              if (enemydir < 0.5) {
                  swarm.enemies(i).move(new Vec2(10, 0))
              }
              if (enemydir > 0.5) {
                  swarm.enemies(i).move(new Vec2(-10, 0))
              }
              
              if(swarm.enemies(i).showPos.x > 960) {
                swarm.enemies(i).move(new Vec2(-5, 0))
              }
              if(swarm.enemies(i).showPos.x < 0) {
                swarm.enemies(i).move(new Vec2(5, 0))
              }
              swarm.enemies(i).move(new Vec2(0, 0.5))
              
              if(swarm.enemies(i).showPos.y > 960) {
                swarm.enemies(i).showPos.y = -10
              }            
            //}
          }
          
          //rate of fire of enemyswarm
          val enemyrof = math.random()
          if (enemyrof < 0.05) {
            enemybullets += swarm.shoot()
          }
          
          //special_enemy attributes
          val specialSpawnRate = math.random()
          if(specialSpawnRate < 0.001 && numOfSpecial < 1) {
            numOfSpecial += 1
            val specialEnemy = new SpecialEnemy(special_enemy1, new Vec2(425, 0), bulletimg)
            specialSwarm.enemies += specialEnemy
          }
          
          for(j <- 0 until specialSwarm.enemies.length) {
            if(specialSwarm.enemies(j).showPos.x < 0) {
              specialSwarm.enemies(j).move(new Vec2(playerspeed/2, 0))
            }
            if(specialSwarm.enemies(j).showPos.x > 1000 - 150) {
              specialSwarm.enemies(j).move(new Vec2(-playerspeed/2, 0)) 
            }
            if(specialSwarm.enemies(j).showPos.y < 0) {
              specialSwarm.enemies(j).move(new Vec2(0, playerspeed/2))
            }
            if(specialSwarm.enemies(j).showPos.y > 900-150) {
              specialSwarm.enemies(j).move(new Vec2(0, -playerspeed/2))
            }
          }
          
          //specialEnemy rof          
          val specialrof = math.random()
          if(specialrof < 0.02) {
            enemybullets += specialSwarm.shoot
          }
                    
          
          //playerbullet/specialEnemy collision
          for(j <- 0 until specialSwarm.enemies.length) {
            for(i <- 0 until playerbullets.length) {
              if(playerbullets(i).returnPos.y + 20 > specialSwarm.enemies(j).showPos.y && playerbullets(i).returnPos.y < specialSwarm.enemies(j).showPos.y + 150 &&
                 playerbullets(i).returnPos.x + 20 > specialSwarm.enemies(j).showPos.x && playerbullets(i).returnPos.x < specialSwarm.enemies(j).showPos.x + 150) 
              {
                specialHit += 1
                score += 100 * wavesCleared
                playerbullets -= playerbullets(i)
                if(specialHit == 1) {
                  specialSwarm.enemies(j) = new SpecialEnemy(special_enemy2, new Vec2(specialSwarm.enemies(j).showPos.x, specialSwarm.enemies(j).showPos.y), bulletimg)
                }
                if(specialHit == 2) { 
                  specialSwarm.enemies(j) = new SpecialEnemy(special_enemy3, new Vec2(specialSwarm.enemies(j).showPos.x, specialSwarm.enemies(j).showPos.y), bulletimg)
                }
                if(specialHit == 3) {
                  specialSwarm.enemies -= specialSwarm.enemies(j)
                  numOfSpecial = 0
                  specialHit = 0
                } 
              }
            }
          }
          
          //player/specialEnemy collision
          for(j <- 0 until specialSwarm.enemies.length) {
            if(specialSwarm.enemies(j).showPos.y + 150 > player.showPos.y && specialSwarm.enemies(j).showPos.y < player.showPos.y + 48 && 
               specialSwarm.enemies(j).showPos.x + 150 > player.showPos.x && specialSwarm.enemies(j).showPos.x < player.showPos.x + 61) 
            {
              playerHitPerLife -= 1         
              if (playerHitPerLife == 1) {
                // damaged sprite:
                player = new Player(playerdamaged, new Vec2(470,820), bulletimg)
                damaged = true
              }

              if (playerHitPerLife <= 0) {
                player = new Player(img, new Vec2(470,820), bulletimg)
                playerHitPerLife = 2
                playerLives -= 1
                damaged = false
              }

              score += 100 * wavesCleared
              
              specialHit += 1
              if(specialHit == 1) {
                specialSwarm.enemies(j) = new SpecialEnemy(special_enemy2, new Vec2(specialSwarm.enemies(j).showPos.x, specialSwarm.enemies(j).showPos.y), bulletimg)
              }
              if(specialHit == 2) { 
                specialSwarm.enemies(j) = new SpecialEnemy(special_enemy3, new Vec2(specialSwarm.enemies(j).showPos.x, specialSwarm.enemies(j).showPos.y), bulletimg)
              }
              if(specialHit == 3) {
                specialSwarm.enemies -= specialSwarm.enemies(j)
                numOfSpecial = 0
                specialHit = 0
              }
            }
          }
          
                    
          //tracks swarm movement
          println(moveLength)
          moveLength -= interval
          if(moveLength <= 0) {
            moveLength = 1.0
          }
          //tracks playershots per second
          shotCooldown -= interval
          if(shotCooldown <= 0) {
            shotCooldown = 1.0
            shotAlready = 0
          }          
          
          //if player lives = 0, gameOver is true
          if(playerLives <= 0) {
            gameOver = true
          }
          
          //display end screen and end game when gameOver is true
          if (gameOver) {
            g.drawImage(backgroundimg_jk, 0, 0)
            //g.drawImage(endscreenimg2, 1000/2 - 400/2, 0)
            g.fill = Color.Black
            g.fillText("GAME OVER", 450, 540, 100)
            g.fillText("FINAL SCORE: " + score.toString, 450, 580, 100)
            g.fillText("RESTART APP TO TRY AGAIN", 450, 620)
            timer.stop
          }
          
          //new swarm when all enemies removed
          if(swarm.enemies.length == 0) {
            waveNum += 1
            swarm = new EnemySwarm(swarmRows + waveNum, swarmCols) 
            score += (1000 * wavesCleared)
            wavesCleared += 1
          }
                    
          //remove bullets offscreen
          for(i <- 0 until enemybullets.length) {
            if(enemybullets(i).returnPos.y > 1100 || enemybullets(i).returnPos.y < -100) {
              enemybullets -= enemybullets(i)
            }
          }
          
          for(i <- 0 until playerbullets.length) {
            if(playerbullets(i).returnPos.y > 1100 || playerbullets(i).returnPos.y < -100) {
              playerbullets -= playerbullets(i)
            }
          }

          
          //enemybullets/player collision
          for(i <- 0 until enemybullets.length) {
            if(enemybullets(i).returnPos.y + 20 > player.showPos.y && enemybullets(i).returnPos.y < player.showPos.y + 48 && 
               enemybullets(i).returnPos.x + 20 > player.showPos.x && enemybullets(i).returnPos.x < player.showPos.x + 61) 
            {
              playerHitPerLife -= 1
              
              if (playerHitPerLife == 1) {
                // damaged sprite:
                player = new Player(playerdamaged, new Vec2(470,820), bulletimg)
                damaged = true
              }

              if (playerHitPerLife <= 0) {
                player = new Player(img, new Vec2(470,820), bulletimg)
                playerHitPerLife = 2
                playerLives -= 1
                damaged = false
              }

              
              enemybullets = Buffer[Bullet]()
              playerbullets = Buffer[Bullet]()
            }
          }
          
          //playerbullets/enemy collision
          for(j <- 0 until playerbullets.length) {
            for(i <- 0 until swarm.enemies.length) {
              if(playerbullets(j).returnPos.y + 20 > swarm.enemies(i).showPos.y && playerbullets(j).returnPos.y < swarm.enemies(i).showPos.y + 50 && 
                 playerbullets(j).returnPos.x + 20 > swarm.enemies(i).showPos.x && playerbullets(j).returnPos.x < swarm.enemies(i).showPos.x + 50)
              {
                swarm.enemies -= swarm.enemies(i)
                playerbullets -= playerbullets(j)
                score += (10 * wavesCleared)
                                
                /*hit = true
                for(k <- 0 to 8) {
                  for(l <- 0 to 6) {
                      g.drawImage(explosionImg, k*64, l*64, 64, 64, swarm.enemies(i).showPos.x, swarm.enemies(i).showPos.y, 64, 64)
                  }
                }
                hit = false 
                hit = true
                  if(hit) {
                    for(e <- 0 to 5) {
                      g.drawImage(explosionImg, 64, 64, 64, 64, swarm.enemies(i).showPos.x, swarm.enemies(i).showPos.y, 64, 64)
                    }
                  }
                */
              }
            }
          }
                 
          //player/enemy collision
          for(i <- 0 until swarm.enemies.length) {
            if(swarm.enemies(i).showPos.x + 50 > player.showPos.x && swarm.enemies(i).showPos.x < player.showPos.x + 48 &&
               swarm.enemies(i).showPos.y + 50 > player.showPos.y && swarm.enemies(i).showPos.y < player.showPos.y + 61)
            {
              playerHitPerLife -= 1

              if (playerHitPerLife == 1) {
                // damaged sprite:
                player = new Player(playerdamaged, new Vec2(470,820), bulletimg)
                damaged = true
              }

              if (playerHitPerLife <= 0) {
                player = new Player(img, new Vec2(470,820), bulletimg)
                playerHitPerLife = 2
                playerLives -= 1
                damaged = false
              }

              enemybullets = Buffer[Bullet]()
              playerbullets = Buffer[Bullet]()
              
              swarm.enemies -= swarm.enemies(i)
              score += (10 * wavesCleared)
              
              //g.drawImage(explosionImg, swarm.enemies(i).showPos.x, swarm.enemies(i).showPos.y, 64, 64, swarm.enemies(i).showPos.x, swarm.enemies(i).showPos.y, 64, 64)
            }
          }
          
          //playerbullet/enemybullet collision
          for(i <- 0 until enemybullets.length) {
            for(j <- 0 until playerbullets.length) {
              if(enemybullets(i).returnPos.x + 20 > playerbullets(j).returnPos.x && enemybullets(i).returnPos.x < playerbullets(j).returnPos.x + 20 &&
                 enemybullets(i).returnPos.y + 20 > playerbullets(j).returnPos.y && enemybullets(i).returnPos.y < playerbullets(j).returnPos.y + 20)
              {
                enemybullets -= enemybullets(i)
                playerbullets -= playerbullets(j)
              }
            }
          }
          //actions for movement input -- player and specialEnemy
          if (lefttouch) {
            player.moveLeft()
            player = new Player(playerleft, new Vec2(player.showPos.x, player.showPos.y), bulletimg)
            printf(player.showPos.toString + "\n")
            player.display(g)
            if(numOfSpecial == 1) {
              specialSwarm.enemies(0).move(new Vec2(playerspeed/2,0))
            }
          } else if (righttouch) {
            player.moveRight()
            player = new Player(playerright, new Vec2(player.showPos.x, player.showPos.y), bulletimg)
            printf(player.showPos.toString + "\n")
            player.display(g)
            if(numOfSpecial == 1) {
              specialSwarm.enemies(0).move(new Vec2(-playerspeed/2,0))
            }
          } else {
            player = new Player(img, new Vec2(player.showPos.x, player.showPos.y), bulletimg)
          }
          
          if (uptouch) {
            player.moveUp()
            printf(player.showPos.toString + "\n")
            player.display(g)
            if(numOfSpecial == 1) {
              specialSwarm.enemies(0).move(new Vec2(0,playerspeed/2))
            }

          }
          if (downtouch) {
            player.moveDown()
            printf(player.showPos.toString + "\n")
            player.display(g)
            if(numOfSpecial == 1) {
              specialSwarm.enemies(0).move(new Vec2(0,-playerspeed/2))
            }

          }
            
        }
      })
      timer.start
      canvas.requestFocus() 
    }
  }
}