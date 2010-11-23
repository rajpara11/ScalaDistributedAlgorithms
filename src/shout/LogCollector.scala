 package shout

import scala.actors.Actor
import scala.actors.Actor._

class LogCollector(numNodes:Int) extends Actor {
	var log:String = ""
	var nodeCount:Int = 0
		
	def act(){
		loop{
			react{
				case message:LogMessage =>
					addMessageToLog(message.logMessage)
			}
		}
	}
	
	def addMessageToLog(messageText:String){
		log += messageText
		nodeCount = nodeCount + 1
		if (nodeCount == numNodes) serializeLog()
	}
	
	def serializeLog(){
		val out = new java.io.FileWriter("simulationLog.txt")
		out.write(log)
		out.close()
		println(log)
	}
}