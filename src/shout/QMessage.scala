package shout

case object QMessage

class QMessage(sender:Int) {
	val senderId = sender
}