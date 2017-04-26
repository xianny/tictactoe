import scala.io.StdIn

object Game {

  val cols = Seq("1", "2", "3")
  val rows = Seq("A", "B", "C")
  val default = "_"

  val squares = crossX(rows, cols)
  val colSquares = cols.map(c => crossX(rows, Seq(c)))
  val rowSquares = rows.map(r => crossX(Seq(r), cols))
  val diagonals = Seq(inverse(rows, cols), inverse(rows.reverse, cols))
  val wins: Seq[Seq[String]] = colSquares ++ rowSquares ++ diagonals

  def token(player: Boolean) = if (player) "X" else "O"

  def main(args: Array[String]) = {
    val gameState = squares.map((_, default)).toMap
    prettyPrintGame(gameState)
    run(true, gameState)
  }

  def run(player: Boolean, currState: Map[String, String]): Unit = {
    val nextState = playTurn(player, currState, player)
    prettyPrintGame(nextState)

    findWinner(nextState) match {
      case None => run(!player, nextState)
      case Some("ERR") => println("Illegal game state. Ending game.")
      case Some("DRAW") => println("Draw! Ending game.")
      case Some(token) => println(s"Game over, player `$token` wins")
    }
  }

  def playTurn(player: Boolean, currState: Map[String, String], playAI: Boolean): Map[String, String] = { 
    val input = if (playAI) autoMove(currState, player) else playerMove(player: Boolean)
    updateTurn(player, currState, input, playAI)
  }

  def playerMove(player: Boolean) = {
    println("Enter square to fill, e.g. A1")
    readLine(s"Player `${token(player)}`: ")
  }

  def autoMove(currState: Map[String, String], player: Boolean) = {
    val validSquares = squares.filter(square => currState.get(square) == Some(default))
    val rand = new scala.util.Random
    validSquares(rand.nextInt(validSquares.length - 1))
  }
  
  def updateTurn(player: Boolean, currState: Map[String, String], input: String, playAI: Boolean) = {
      if (squares.contains(input) && currState.get(input) == Some(default)) { 
        currState + (input -> token(player))
      } else {
        if (!playAI) { println("Invalid input") }
        playTurn(player, currState, playAI)
      }
  }


  def findWinner(state: Map[String, String]): Option[String] = {
    val xs = state.filter(_._2 == "X").keys.toSet
    val os = state.filter(_._2 == "O").keys.toSet

    val xWin = wins.exists(set => set.toSet.subsetOf(xs))
    val oWin = wins.exists(set => set.toSet.subsetOf(os))

    if (xWin && oWin) {
      Some("ERR") 
    } else if (xWin) {
      Some("X")
    } else if (oWin) {
      Some("O")
    } else if (xs.size + os.size == state.size) {
      Some("DRAW")
    } else {
      None
    }
  }

  def crossX(as: Seq[String], bs: Seq[String]): Seq[String] = {
    for (a <- as;
         b <- bs) yield
      a+b
  }

  def inverse(as: Seq[String], bs: Seq[String]) = { 
    for (i <- 0 until as.length;
         a = as(i);
         b = bs(bs.length - 1 - i)) yield
         a+b
  }

  def prettyPrintGame(state: Map[String, String]) = {
    val _rows = for (
         (sqs, i) <- squares.sliding(rows.length, rows.length).zipWithIndex;
         values = sqs.map(sq => state.get(sq).getOrElse("_"));
         label = rows(i)) yield label ++ values 

    val _cols = " " ++ cols

    println(_cols.mkString(" | "))
    _rows.foreach(r => println(r.mkString(" | ")))
  }
}
