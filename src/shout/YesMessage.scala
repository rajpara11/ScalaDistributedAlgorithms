package shout

case object YesMessage

class YesMessage(sender:Int) {
	val senderId = sender
}