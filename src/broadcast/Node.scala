package broadcast

import scala.actors.Actor
import scala.actors.Actor._
import scala.xml._
import java.util.Calendar

class Node(val id:Int, logCollector:LogCollector, isInitiator:Boolean = false) extends Actor {
	
	val idle:String = "idle"
	val done:String = "done"
	
	var state:String = idle
	var neighbors:List[Node] = null
	
	var log:String = ""
	
	def act(){
		val messageToSend = new Message(id)
		if (isInitiator){
			println("Initiator node" + id + " has awakened")
			// Send message to all the neighbors
			for (neighbor <- neighbors){
				neighbor ! messageToSend
				println("Node" + id + " sending message to Node" + neighbor.id)
				logOutgoing(neighbor.id)
			}
			state = done
			logCollector ! new LogMessage(finalizeLog())
			println("Node" + id + " is done")
		}
		loop{
			react{
				case message:Message =>
					if (state == idle){
						logIncoming(message.senderId)
						// send message to [N(x) - sender]
						for (neighbor <- neighbors){
							if (neighbor != null && message.senderId != neighbor.id){
								neighbor ! messageToSend
								println("Node" + id + " sending message to Node" + neighbor.id)
								logOutgoing(neighbor.id)
							}
						}
						// Become Done
						state = done
						println("Node" + id + " is done")
						logCollector ! new LogMessage(finalizeLog())
					} else if (state == done){
						// do nothing, already done
					}
					
			}
		}
	}
	
	def setNeighbors(neighborsList:List[Node]){
		neighbors = neighborsList
	}
	
	def logOutgoing(neighbor:Int){
		val time:Long = System.currentTimeMillis()
		log += "<message timestamp='" + time + "' sentTo='" + neighbor + "' ></message>"
	}
	
	def logIncoming(neighbor:Int){
		val time:Long = System.currentTimeMillis()
		log += "<message timestamp='" + time + "' receivedFrom='" + neighbor + "' ></message>"
	}
	
	def finalizeLog():String = {
		"<node id='" + id + "'>" + log + "</node>"
	}
}