package hello

import scala.actors.Actor
import scala.actors.Actor._

case object Stop

/**
 * Ping pong example.
 *
 * @author  Philipp Haller
 * @version 1.1
 */
object pingpong extends Application {
  val pong = new Pong
  val ping = new Ping(100000, pong)
  ping.start
  pong.start
}
