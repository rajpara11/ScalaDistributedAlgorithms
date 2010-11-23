package saturation_minFinding

case object ActivateMessage

class ActivateMessage(sender:Int) {
	val senderId = sender
}