MOVES = [(1,1), (1,2), (1,3), (2,1), (2,2), (2,3), (3,1), (3,2), (3,3)]
WINS = [set([(1,1), (1,2), (1,3)]),
        set([(2,1), (2,2), (2,3)]),
        set([(3,1), (3,2), (3,3)]),
        set([(1,1), (2,1), (3,1)]),
        set([(1,2), (2,2), (3,2)]),
        set([(1,3), (2,3), (3,3)]),
        set([(1,1), (2,2), (3,3)]),
        set([(1,3), (2,2), (3,1)])]

def new_game():
    return []

def next_player(game):
    if len(game) % 2 == 0:
        return 1
    else:
        return 2

def twos(game):
    return set([ game[i] for i in range(1, len(game), 2) ])

def ones(game):
    return set([ game[i] for i in range(0, len(game), 2) ])

def result(game):
    for winset in WINS:
        if winset.issubset(ones(game)):
            return 1
        elif winset.issubset(twos(game)):
            return 2

    return None

def add_move(move, game):
    if move in MOVES:
        game.append(move)

    return game
