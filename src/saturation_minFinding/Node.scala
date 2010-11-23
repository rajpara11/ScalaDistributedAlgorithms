package saturation_minFinding

import scala.actors.Actor
import scala.actors.Actor._
import scala.xml._
import java.util.Calendar
import scala.collection.mutable.ListBuffer

class Node(val id:Int, logCollector:LogCollector, isInitiator:Boolean = false) extends Actor {
	
	val available:String = "available"
	val active:String = "active"
	val processing:String = "processing"
	val saturated:String = "saturated"
	val minimum:String = "minimum"
	val large:String = "large"
	
	var value:Int = 0;
	var min:Int = 0;
	
	var parent:Node = null
	var senderNode:Node = null
	
	var state:String = available
	var neighbors:List[Node]=null
	var neighborsList:List[Node]=null

	
	var log:String = ""
	
	def act(){
		val activateMessageToSend = new ActivateMessage(id)
		val messageToSend = new Message(id)
		val notificationToSend = new NotificationMessage(id)
		
		if (isInitiator){
			println("Initiator node" + id + " has awakened")

			for (neighbor <- neighbors){
				if ((neighbor != null)){
					neighbor ! activateMessageToSend
					println("Node" + id + " sending Activate message to Node" + neighbor.id)
					logOutgoing(neighbor.id, "activate")
				}
			}
			initialize()
			neighborsList = neighbors
			if (neighborsList.length == 1) {
				prepareMessage(messageToSend)
				parent = neighborsList.head
				parent ! messageToSend
				println("Node" + id + " sending Message message to Node" + parent.id)
				logOutgoing(parent.id, "return")
				state = processing
				
			}
			else {
				state = active
			}

		}
		loop{
			react{
				case activateMessage:ActivateMessage =>
					logIncoming(activateMessage.senderId, "activate")
					if (state == available){

						for (neighbor <- neighbors){
							if ((neighbor != null) && (activateMessage.senderId != neighbor.id)){
								neighbor ! activateMessageToSend
								println("Node" + id + " sending Activate message to Node" + neighbor.id)
								logOutgoing(neighbor.id, "activate")
							}
							
						}
						initialize()
						neighborsList = neighbors				
						if (neighborsList.length == 1) {
	
							prepareMessage(messageToSend)
							parent = neighborsList.head
							parent ! messageToSend
							println("Node" + id + " sending Message message to Node" + parent.id + " with min " + messageToSend.value)
							logOutgoing(parent.id, "message")
							state = processing
							
						}
						else {
							state = active
						}
						
						
					} 
					
				case message:Message =>	
					if (state == active){
						logIncoming(message.senderId, "message")
						processMessage(message)
						neighborsList = neighborsList.remove((neighbor) => neighbor.id == message.senderId)
						
						if (neighborsList.length == 1) {
							prepareMessage(messageToSend)
							parent = neighborsList.head
							parent ! messageToSend
							println("Node" + id + " sending Message message to Node" + parent.id + " with min " + messageToSend.value)
							logOutgoing(parent.id, "message")
							state = processing
							
						}
					}
					else if (state == processing) {
						processMessage(message)
						resolve(notificationToSend)
						
					}
				case notification:NotificationMessage =>	
					if (state == processing) {
						for (neighbor <- neighbors){
							if ((neighbor != null) && (parent.id != neighbor.id)){
								neighbor ! notification
								println("Node" + id + " sending Notification message to Node" + neighbor.id + " with min " + notification.value)
								logOutgoing(neighbor.id, "notification")
							}
							
						}
						
						if (value == notification.value){
							state = minimum
						} 
						else{
							state = large
						}
						logStateDone()
						
					}

			}
		}
	}
	
	def initialize() {
		min = value
	}
	def prepareMessage(message:Message) {
		message.setValue(min)
	}
	def processMessage(message:Message) {
		min = Math.min(min, message.value)
	}
	def resolve(notification:NotificationMessage) {
		notification.setValue(min)
		
		for (neighbor <- neighbors){
			if ((neighbor != null) && (parent.id != neighbor.id)){
				neighbor ! notification
				println("Node" + id + " is saturated and sending Notification message to Node" + neighbor.id + " with min " + notification.value)
				logOutgoing(neighbor.id, "notification")
			}
			
		}
		
		if (value == notification.value) {
			state = minimum
		}
		else {
			state = large
		}
		logStateDone()
	}
	
	def setNeighbors(neighborsList:List[Node]){
		neighbors = neighborsList
	}
	
	def setValue(valToSet:Int) {
		value = valToSet
	}
	
	def logStateDone(){
		println("Node" + id + " is " + state)
		
		logCollector ! new LogMessage(finalizeLog())
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