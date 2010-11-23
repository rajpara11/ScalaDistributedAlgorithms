package shout

case object NoMessage

class NoMessage(sender:Int) {
	val senderId = sender
}