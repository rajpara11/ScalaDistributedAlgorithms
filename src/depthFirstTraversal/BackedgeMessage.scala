package depthFirstTraversal

case object BackedgeMessage

class BackedgeMessage(sender:Int) {
	val senderId = sender
}