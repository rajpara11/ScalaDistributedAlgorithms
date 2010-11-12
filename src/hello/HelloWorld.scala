package hello

object HelloWorld {

  def main(args: Array[String]): Unit = {
	  println("Hello wolf!!!")
	  println(wolfFunction(5,6))
  }
  
  def wolfFunction(number1:Int,number2:Int): Int = {
	  return number1 + number2
  }

}