class News():

    FIELD_SEP="\x00"

    def __init__(self, newsId, type):
        self.newsId=newsId
        self.type=type



    def __repr__(self):
        return str(self.newsId)+\
        self.FIELD_SEP+\
        str(self.type)

    def __str__(self):
        return str(self.newsId)+\
        self.FIELD_SEP+\
        str(self.type)