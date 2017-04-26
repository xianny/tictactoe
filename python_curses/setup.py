try:
    from setuptools import setup
except ImportError:
    from distutils.core import setup

config = {
        'description': 'Tic-tac-toe in python and ncurses',
        'author': 'Xianny Ng',
        'install_requires': [],
        'packages': ['tictactoe'],
        'scripts': [],
        'name': 'tictactoe',
        'entry_points': {
            'console_scripts': {
                'tictactoe': 'tictactoe.__main__:main'
                }
            }
        }

setup(**config)
