

class KeyProp():

    def __init__(self, id, keyPattern, desc, maxSize, ttl):
        self.id = id
        self.keyPattern = keyPattern
        self.desc = desc
        self.maxSize = maxSize
        self.ttl = ttl


class NewsRedisKey():

    RANK_TOPN_NEWS = KeyProp(31, 'rank:news', 'topN news', 100, 8*60*60)






