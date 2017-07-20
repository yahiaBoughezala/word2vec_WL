
class UserNewsMsg():
    '''
    User News Msg
    '''

    def __init__(self, uid, channelId, ts):
        self.uid = uid
        self.channelId = channelId
        self.ts = ts

    def __str__(self):
        return "UserNewsMsg{" + "uid='" + \
        self.uid + '\'' + \
        ", channelId=" + \
        self.channelId + \
        ", ts=" + self.ts + \
        '}';