

class NewsDTO():

    FIELD_SEP=u'0000'

    def __init__(self, newsId, type, sourceType, displayTime):
        self.newsId=newsId
        self.type=type
        self.sourceType=sourceType
        self.displayTime=displayTime


    def __str__(self):
        str(self.newsId)+\
        self.FIELD_SEP+\
        str(type)+\
        self.FIELD_SEP+\
        str(self.sourceType)+\
        self.FIELD_SEP+\
        str(self.displayTime)