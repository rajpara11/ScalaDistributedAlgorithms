package shout

case object Message

class Message(sender:Int) {
	val senderId = sender
}