from enum import Enum, unique

@unique
class Policy(Enum):
    NONE = 0
    SOURCE = 1
    EXCLUDE = 2
    BINARY = 3
