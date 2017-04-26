from .game import *

win = list(WINS[0])
ones_win_game = [win[0], 33, win[1], 33, win[2]]
twos_win_game = [33, win[0], 33, win[1], 33, win[2]]

def test_result():
    game = new_game()
    assert result(game) == None

    game = ones_win_game
    assert result(game) == 1

    game = twos_win_game
    assert result(game) == 2

def test_add_move_and_next_player():
    game = new_game()

    game1 = add_move(win[0], game)
    assert len(game1) == 1
    assert next_player(game1) == 2

    game2 = add_move(44, game1)
    assert len(game2) == 1
    assert next_player(game2) == 2
