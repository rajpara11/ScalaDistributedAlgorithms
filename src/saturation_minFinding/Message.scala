package saturation_minFinding

case object Message

class Message(sender:Int) {
	val senderId = sender
	var value:Int = 0
	
	def setValue(valToSet:Int) {
		value = valToSet
	}
}