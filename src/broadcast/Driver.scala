package broadcast

object Driver extends Application {
	val logCollector = new LogCollector(5)
	logCollector.start
	
	val node1 = new Node(1, logCollector, true)
	val node2 = new Node(2, logCollector)
	val node3 = new Node(3, logCollector)
	val node4 = new Node(4, logCollector)
	val node5 = new Node(5, logCollector)
	
	node1.setNeighbors(List(node2))
	node2.setNeighbors(List(node1,node3))
	node3.setNeighbors(List(node2,node4))
	node4.setNeighbors(List(node3,node5))
	node5.setNeighbors(List(node4))
	
	node1.start
	node2.start
	node3.start
	node4.start
	node5.start
}