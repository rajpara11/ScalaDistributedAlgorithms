package saturation_minFinding

object Driver extends Application {
	val logCollector = new LogCollector(5)
	logCollector.start
	
	val node1 = new Node(1, logCollector, true)
	node1.setValue(1)
	val node2 = new Node(2, logCollector)
	node2.setValue(2)
	val node3 = new Node(3, logCollector)
	node3.setValue(3)
	val node4 = new Node(4, logCollector)
	node4.setValue(4)
	val node5 = new Node(5, logCollector)
	node5.setValue(5)
	
	node1.setNeighbors(List(node2, node3))
	node2.setNeighbors(List(node1, node4, node5))
	node3.setNeighbors(List(node1))
	node4.setNeighbors(List(node2))
	node5.setNeighbors(List(node2))
	
	node1.start
	node2.start
	node3.start
	node4.start
	node5.start
}