package saturation_minFinding

case object NotificationMessage

class NotificationMessage(sender:Int) {
	val senderId = sender
	var value:Int = 0
	
	def setValue(valToSet:Int) {
		value = valToSet
	}
}