import fwk,os
def entry():
    fwk.load_cmds(os.path.dirname(__file__), 'cli')
