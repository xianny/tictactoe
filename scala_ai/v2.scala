val USE_AI = false
CLI.run(Game.InitialState, USE_AI)

object CLI {

  import Game.prettyPrint

  def run(game: Game, useAI: Boolean): Unit = {
    prettyPrint(game)
    game.result match {
      case Win(token) => println(s"Game over, player `$token` wins")
      case Draw       => println("Draw! Ending game.")
      case Error      => println("Illegal game state. Ending game.")
      case Continue   => 
        val input = if (useAI) GameAI.nextMove(game) else prompt(game.player)
        game.update(input) match {
          case Some(nextState) => run(nextState, !useAI)
          case None => 
            println(s"Error: Something went wrong")
            run(game, useAI)
        }
    }
  }

def prompt(player: Token) = {
    println("Enter square to fill, e.g. A1")
    readLine(s"Player `$player`: ")
  }
}


sealed trait Result 
case object Draw extends Result
case object Error extends Result
case object Continue extends Result
case class Win(player: Token) extends Result

sealed trait Token
case object X extends Token
case object O extends Token

case class Game(board: Map[String, Token], player: Token) {

  def update(coords: String): Option[Game] = 
    if (isValid(coords)) Some(safeUpdate(coords)) else None

  private def safeUpdate(coords: String) = {
    val newBoard = board + (coords -> player)
    Game(newBoard, nextPlayer)
  }

  def result: Result = {
    val (xs, os) = board.partition(_._2 == X)
    def xWin = Game.hasWin(xs.keys.toSeq)
    def oWin = Game.hasWin(os.keys.toSeq)

    if (xWin && oWin) {
      Error
    } else if (xWin) {
      Win(X)
    } else if (oWin) {
      Win(O)
    } else if (xs.size + os.size == Game.squares.size) {
      Draw
    } else {
      Continue
    }
  }

  def isValid(coords: String) = isExists(coords) && isEmpty(coords)
  def isExists(coords: String) = Game.squares.contains(coords)
  def isEmpty(coords: String) = board.get(coords).isEmpty

  def nextPlayer = player match {
    case X => O
    case O => X
  }
}

object Game {

  def InitialState = Game(Map(), X)

  def hasWin(coords: Seq[String]) = wins.exists(set => set.toSet.subsetOf(coords.toSet))

  private val cols = Seq("1", "2", "3")
  private val rows = Seq("A", "B", "C")

  val squares = crossX(rows, cols)
  
  private val colSquares = cols.map(c => crossX(rows, Seq(c)))
  private val rowSquares = rows.map(r => crossX(Seq(r), cols))
  private val diagonals = Seq(inverse(rows, cols), inverse(rows.reverse, cols))
  
  val wins: Seq[Seq[String]] = diagonals ++ colSquares ++ rowSquares

  def prettyPrint(game: Game) = {
    val _rows = for (
         (sqs, i) <- squares.sliding(rows.length, rows.length).zipWithIndex;
         values = sqs.map(sq => game.board.get(sq).getOrElse("_"));
         label = rows(i)) yield label ++ values 

    val _cols = " " ++ cols

    println(_cols.mkString(" | "))
    _rows.foreach(r => println(r.mkString(" | ")))
  }

  private def crossX(as: Seq[String], bs: Seq[String]): Seq[String] = {
    for (a <- as;
         b <- bs) yield
      a+b
  }

  private def inverse(as: Seq[String], bs: Seq[String]) = { 
    for (i <- 0 until as.length;
         a = as(i);
         b = bs(bs.length - 1 - i)) yield
         a+b
  }

}

object GameAI {

  type PreviousMove = String
  case class StatefulGame(game: Game, prev: PreviousMove)

  def nextMove(game: Game): String = {
    possibleNextStates(game)
      .maxBy(sg => scoreGame(sg.game))
      .prev
  }

  def scoreGame(game: Game): Double = {
    game.result match {
      case Draw             => 0
      case Error            => 0
      case Win(tok: Token)  => if (tok == game.player) -1 else 1
      case Continue         =>
        // recursive case
        possibleNextStates(game)
          .map(sg => scoreGame(sg.game))
          .sum * (-0.5)
    }
  }

  def possibleNextStates(game: Game): Seq[StatefulGame] = for {
      emptySq <- Game.squares.diff(game.board.keys.toSeq)
      nextGame <- game.update(emptySq)
  } yield StatefulGame(nextGame, emptySq)
}