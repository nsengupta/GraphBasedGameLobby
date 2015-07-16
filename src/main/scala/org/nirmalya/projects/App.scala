package org.nirmalya.projects

/**
 * @author ${user.name}
 */
object Solution {

  /*def main(args: Array[String]) {
    /* Enter your code here. Read input from STDIN. Print output to STDOUT. Your class should be named Solution      */

    val times = io.Source.stdin.getLines.toList.head.toInt

    (0 to {
      if ((times < 0) || (times > 50))
        -1
      else
        (times-1)
    }).foreach(i => println("Hello World"))
  }*/

  def main(args: Array[String]): Unit = {

    val times = io.Source.stdin.getLines.toList.splitAt(1)

    val x = times._1.head.toInt

    val s = times._2.map(e => e.toInt).filter(i => ((i >= 1) && ((i <= 100))))

    println(f(x,s))

  }

  def f (num: Int, arr: List[Int]): List[Int] = {

    arr.map(i => List.fill(num)(i)).flatten
  }
}
