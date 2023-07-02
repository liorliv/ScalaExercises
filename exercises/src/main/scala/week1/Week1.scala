package week1

import scala.annotation.tailrec

object Week1 {

  def pascal(c: Int, r: Int): Int = if (c == 0 || c == r) 1 else pascal(c - 1, r - 1) + pascal(c, r - 1)

  def balance(chars: List[Char]): Boolean = {
    @tailrec
    def tailrec(chars: List[Char], counter: Int): Boolean = {
      if (counter < 0) false
      else chars.headOption match {
        case Some(value) =>
            val newCounter = if (value.equals('(')) counter + 1 else if (value.equals(')')) counter - 1 else counter
            tailrec(chars.tail, newCounter)
        case None => true
      }
    }

    tailrec(chars, 0)
  }

  def countChange(money: Int, coins: List[Int]): Int = {
    if (money < 0 || coins.isEmpty) 0
    else if (money == 0) 1
    else countChange(money, coins.tail) + countChange(money - coins.head, coins)
  }


}
