
class TopNewsUpdate():

    def __init__(self, ts, newsUpdateStatus):
        self.ts = ts
        self.newsUpdateStatus = newsUpdateStatus

    def __str__(self):
        return "TopNewsUpdate{" + "ts='" + \
        self.ts + '\'' + \
        ", newsUpdateSuccess=" + \
        self.newsUpdateSuccess


class NewsUpdateStatus():

    SUCCESS = 'SUCCESS'
    FAILED = 'FAILED'