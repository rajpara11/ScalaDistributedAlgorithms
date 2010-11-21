package depthFirstTraversal

import scala.actors.Actor
import scala.actors.Actor._
import scala.xml._
import java.util.Calendar

class Node(val id:Int, logCollector:LogCollector, isInitiator:Boolean = false) extends Actor {
	
	val idle:String = "idle"
	val visited:String = "done"
	val done:String = "done"
	
	var initiator:Boolean = false
	var entry:Node = null	
	
	var state:String = idle
	var neighbors:List[Node] = null 
	var unvisited:List[Node] = null;
	
	var log:String = ""
	
	def act(){
		val tokenMessageToSend = new TokenMessage(id)
		val returnMessageToSend = new ReturnMessage(id)
		val backedgeMessageToSend = new BackedgeMessage(id)

		unvisited = neighbors;
		
		if (isInitiator){
			println("Initiator node" + id + " has awakened")
			initiator = true
			// Send token to first neighbor
			visit(tokenMessageToSend, returnMessageToSend)
			
			state = done

		}
		loop{
			react{
				case tokenMessage:TokenMessage =>
					if (state == idle){
						logIncoming(tokenMessage.senderId, "token")
						

						for (neighbor <- neighbors){
							if ((neighbor != null) && (tokenMessage.senderId == neighbor.id)){
								entry = neighbor
								//neighbors = neighbors.remove(neighbor)
							}
							else {
							}
							
						}

						unvisited = neighbors.remove((neighbor) => neighbor.id == tokenMessage.senderId)
						initiator = false
						visit(tokenMessageToSend, returnMessageToSend)
						
					} else if (state == visited){
						
						unvisited = neighbors.remove((neighbor) => neighbor.id == tokenMessage.senderId)
							
						for (neighbor <- neighbors){
							if (neighbor != null && tokenMessage.senderId == neighbor.id){

								neighbor ! backedgeMessageToSend
								println("Node" + id + " sending Backedge message to Node" + neighbor.id)
								logOutgoing(neighbor.id, "backedge")
							}
						}
							
					}
				case returnMessage:ReturnMessage =>	
					visit(tokenMessageToSend, returnMessageToSend)
				case backedgeMessage:BackedgeMessage =>	
					visit(tokenMessageToSend, returnMessageToSend)
			}
		}
	}
	
	def visit( tokenMessage:TokenMessage, returnMessage:ReturnMessage){
		if ((unvisited.length != 0) && (tokenMessage!= null)) {
			unvisited(0) ! tokenMessage
			println("Node" + id + " sending Token message to Node" + unvisited(0).id)
			logOutgoing(unvisited(0).id, "token")
			state = visited
			
			unvisited = unvisited.drop(1)
		}
		else {
			if ((!initiator)&&(returnMessage!=null)&&(entry!=null)){
				entry ! returnMessage
				println("Node" + id + " sending Return message to Node" + entry.id)
				logOutgoing(entry.id, "return")
			}
			state = done
			logCollector ! new LogMessage(finalizeLog())
			println("Node" + id + " is done")
		}
		
	}
	def setNeighbors(neighborsList:List[Node]){
		neighbors = neighborsList
	}
	
	def logOutgoing(neighbor:Int, typeMessage:String){
		val time:Long = System.currentTimeMillis()
		log += "<message timestamp='" + time + "' typeMessage='" + typeMessage + "' sentTo='" + neighbor + "' ></message>"
	}
	
	def logIncoming(neighbor:Int, typeMessage:String){
		val time:Long = System.currentTimeMillis()
		log += "<message timestamp='" + time + "' typeMessage='"+ typeMessage + "' receivedFrom='" + neighbor + "' ></message>"
	}
	
	def finalizeLog():String = {
		"<node id='" + id + "'>" + log + "</node>"
	}
}