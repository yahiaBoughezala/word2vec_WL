import yaml

def constructor(loader, node):
    fields = loader.construct_mapping(node)
    return OozieJob(**fields)

yaml.add_constructor(u'!OozieJob', constructor)

class OozieJob(object):

    def __init__(self, app=None, type=None, dependencyLibs=None, hdfsPath=None, name=None, schedules=None, scriptPath=None, script=None):
        self.app = app
        self.type = type
        self.dependencyLibs = dependencyLibs
        self.hdfsPath = hdfsPath
        self.name = name
        self.schedules = schedules
        self.scriptPath = scriptPath
        self.script = script

    def __repr__(self):
        return super(OozieJob, self).__repr__()





