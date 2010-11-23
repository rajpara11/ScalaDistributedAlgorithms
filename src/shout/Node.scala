package shout

import scala.actors.Actor
import scala.actors.Actor._
import scala.xml._
import java.util.Calendar
import scala.collection.mutable.ListBuffer

class Node(val id:Int, logCollector:LogCollector, isInitiator:Boolean = false) extends Actor {
	
	val idle:String = "idle"
	val active:String = "active"
	val done:String = "done"
	
	var initiator:Boolean = false
	var root:Boolean = false
	var counter:Int = 0
	var parent:Node = null
	var senderNode:Node = null
	
	var state:String = idle
	var neighbors:List[Node]=null
	var treeNeighbors:ListBuffer[Node] = ListBuffer()
	
	var log:String = ""
	
	def act(){
		val noMessageToSend = new NoMessage(id)
		val yesMessageToSend = new YesMessage(id)
		val qMessageToSend = new QMessage(id)
		treeNeighbors = ListBuffer()
		
		if (isInitiator){
			println("Initiator node" + id + " has awakened")
			initiator = true
			root = true
			treeNeighbors = ListBuffer()
			for (neighbor <- neighbors){
				if ((neighbor != null)){
					neighbor ! qMessageToSend
					println("Node" + id + " sending Question message to Node" + neighbor.id)
					logOutgoing(neighbor.id, "return")
				}
			}
			counter = 0
			state = active

		}
		loop{
			react{
				case qMessage:QMessage =>
					logIncoming(qMessage.senderId, "question")
					if (state == idle){
						root = false

						for (neighbor <- neighbors){
							if ((neighbor != null) && (qMessage.senderId == neighbor.id)){
								senderNode = neighbor

							}
							
						}
						parent = senderNode                                                                         	
						treeNeighbors+=senderNode
						
						senderNode ! yesMessageToSend
						println("Node" + id + " sending Yes message to Node" + senderNode.id)
						logOutgoing(senderNode.id, "return")
						
						counter = 1
						
						if (counter == neighbors.length) {
							logStateDone()
							state = done
						}
						else {
							for (neighbor <- neighbors){
								if ((neighbor != null) && (qMessage.senderId != neighbor.id)){
									neighbor ! qMessageToSend
									println("Node" + id + " sending Question message to Node" + neighbor.id)
									logOutgoing(neighbor.id, "return")
								}
	
								
							}
							state = active
						}
						
						
					} else if (state == active){
						for (neighbor <- neighbors){
							if ((neighbor != null) && (qMessage.senderId == neighbor.id)){
								senderNode = neighbor

							}
							
						}
						senderNode ! noMessageToSend
						println("Node" + id + " sending No message to Node" + senderNode.id)
						logOutgoing(senderNode.id, "return")
							
					}
					
				case yesMessage:YesMessage =>	
					if (state == active){
						for (neighbor <- neighbors){
							if ((neighbor != null) && (yesMessage.senderId == neighbor.id)){
								senderNode = neighbor

							}
							
						}                

						treeNeighbors+=senderNode
						
						counter = counter + 1
						
						if (counter == neighbors.length) {
							logStateDone()
							state = done
						}
					}
				case noMessage:NoMessage =>	
					if (state == active) {
						counter = counter + 1
						
						if (counter == neighbors.length) {
							logStateDone()
							state = done
						}
					}
			}
		}
	}
	

	def setNeighbors(neighborsList:List[Node]){
		neighbors = neighborsList
	}
	
	def logStateDone(){
		println("Node" + id + " is done")
		
		for (neighbor <- treeNeighbors){
			println("Node" + id + " has tree neighbor " + neighbor.id)
				
		}
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