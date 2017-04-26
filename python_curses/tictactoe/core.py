from .game import ones, twos, new_game, add_move, result, next_player
import curses
from curses import wrapper

START = 2
SIZE_Y = 3
SIZE_X = SIZE_Y * 2
END_Y = START + 3 * (SIZE_Y - 1)
END_X = START + 3 * (SIZE_X - 1)

def token(p):
    if p == 1:
        return 'x'
    else:
        return 'o'

def info_win():
    win = curses.newwin(SIZE_Y * 2, SIZE_X * 7, START, END_X + SIZE_X)
    return win

def board(game=[]):
    board = []
    for begin_y in range(START, END_Y, (SIZE_Y - 1)):
        row = []
        for begin_x in range(START, END_X, (SIZE_X - 1)):
            cell = curses.newwin(SIZE_Y, SIZE_X, begin_y, begin_x)
            cell.border(0, 0, 0, 0, '+', '+', '+', '+')
            row.append(cell)
        board.append(row)

    for (x,y) in ones(game):
        add_tok(board[x-1][y-1], token(1))

    for (x,y) in twos(game):
        add_tok(board[x-1][y-1], token(2))

    return board

def add_tok(cell_win, tok):
    cell_win.addstr(SIZE_Y//2, SIZE_X//2, tok)

def show(board):
    for row in board:
        for cell_win in row:
            cell_win.refresh()

def main(stdscr):
    stdscr.refresh()

    game = new_game()
    winner = result(game)

    while winner == None:
        b = board(game)
        show(b)
        next_move = get_input(stdscr, b, next_player(game))
        game = add_move(next_move, game)
        winner = result(game)

    show(board(game))
    show_winner(stdscr, game)
    stdscr.getch()


def show_winner(stdscr, game):
    win = info_win()
    win.addstr("Player %s wins!\n" % result(game))
    win.refresh()

def get_input(stdscr, board, next_player):
    input_str = "Player %s (%s)'s turn.\nPress enter to select a square." % (next_player, token(next_player))
    win = info_win()
    win.addstr(input_str)
    win.refresh()

    (i, j) = (1,1)

    while 1:
        (_y, _x) = board[i][j].getbegyx()
        (y, x) = (_y + (SIZE_Y//2), _x + (SIZE_X//2))
        stdscr.move(y, x)
        stdscr.refresh()
        c = stdscr.getch()
        if c == curses.KEY_DOWN:
            (i, j) = (i+1, j)
        elif c == curses.KEY_UP:
            (i, j) = (i-1, j)
        elif c == curses.KEY_LEFT:
            (i, j) = (i, j-1)
        elif c == curses.KEY_RIGHT:
            (i, j) = (i, j+1)
        elif c == ord('\n'):
            win.addstr("Made move %s %s" % (i, j))
            win.refresh()
            break

    return (i+1, j+1)


def run():
    wrapper(main)
