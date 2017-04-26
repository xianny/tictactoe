import scala.io.StdIn

object Game {

  val cols = Seq("1", "2", "3")
  val rows = Seq("A", "B", "C")
  val default = "_"

  type GamePosition = Map[String, String]
  type LastMove = String
  case class GameState(position: GamePosition, lastMove: LastMove, player: Boolean)

  val squares = crossX(rows, cols)
  val colSquares = cols.map(c => crossX(rows, Seq(c)))
  val rowSquares = rows.map(r => crossX(Seq(r), cols))
  val diagonals = Seq(inverse(rows, cols), inverse(rows.reverse, cols))
  val wins: Seq[Seq[String]] = colSquares ++ rowSquares ++ diagonals

  def token(player: Boolean) = if (player) "X" else "O"

  def main(args: Array[String]) = {
    val gameState = GameState(squares.map((_, default)).toMap, "", true)
    prettyPrintGame(gameState)
    run(gameState)
  }

  def run(currState: GameState): Unit = {
    val nextState = playTurn(currState)
    prettyPrintGame(nextState)

    findWinner(nextState.position) match {
      case None => run(nextState)
      case Some("ERR") => println("Illegal game state. Ending game.")
      case Some("DRAW") => println("Draw! Ending game.")
      case Some(token) => println(s"Game over, player `$token` wins")
    }
  }

  def playTurn(currState: GameState): GameState = { 
    val input = if (currState.player) playerMove else computerMove(currState)
    updateTurn(currState, input)
  }
  
  def updateTurn(currState: GameState, input: String): GameState = {
      if (squares.contains(input) && currState.position.get(input) == Some(default)) { 
        val nextPosition = currState.position + (input -> token(currState.player))
        GameState(nextPosition, input, !currState.player)
      } else {
        if (currState.player) { println("Invalid input") }
        playTurn(currState)
      }
  }

  def computerMove(currState: GameState): String = {
    possibleNextStates(currState).maxBy(scoreState).lastMove
  }

  def scoreState(currState: GameState): Double = {
    findWinner(currState.position) match {
      case Some("ERR")  => 0
      case Some("DRAW") => 0
      case Some(tok)    => if (tok == token(currState.player)) -1 else 1
      case None         =>
        // recursive case
        possibleNextStates(currState).map(scoreState).sum * (-0.1)
    }
  }

  def possibleNextStates(currState: GameState): Seq[GameState] = for {
      emptySq <- squares.filter(sq => currState.position.get(sq) == Some(default))
      nextPosition = currState.position + (emptySq -> token(currState.player))
  } yield GameState(nextPosition, emptySq, !currState.player)

  def playerMove = {
    println("Enter square to fill, e.g. A1")
    readLine(s"Player `${token(true)}`: ")
  }

  def findWinner(position: GamePosition): Option[String] = {
    val xs = position.filter(_._2 == "X").keys.toSet
    val os = position.filter(_._2 == "O").keys.toSet

    val xWin = wins.exists(set => set.toSet.subsetOf(xs))
    val oWin = wins.exists(set => set.toSet.subsetOf(os))

    if (xWin && oWin) {
      Some("ERR") 
    } else if (xWin) {
      Some("X")
    } else if (oWin) {
      Some("O")
    } else if (xs.size + os.size == position.size) {
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

  def prettyPrintGame(state: GameState) = {
    val _rows = for (
         (sqs, i) <- squares.sliding(rows.length, rows.length).zipWithIndex;
         values = sqs.map(sq => state.position.get(sq).getOrElse("_"));
         label = rows(i)) yield label ++ values 

    val _cols = " " ++ cols

    println(_cols.mkString(" | "))
    _rows.foreach(r => println(r.mkString(" | ")))
  }
}
