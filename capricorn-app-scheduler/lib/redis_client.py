from redis_shard.shard import RedisShardAPI
import redis
from lib.log import MyLogger


class RedisClient():

    def __init__(self, host, port, db=0):
        self.conn = redis.ConnectionPool(host=host,port=port,db=db)
        self.redis= redis.Redis(connection_pool=self.conn)
        self.logger = MyLogger.getLogger()

    def getConnection(self):
        return self.redis




class ShardRedisClient():

    def __init__(self, server):
        try:
            self.client = RedisShardAPI(server)
        except:
            raise Exception, 'Failed to connect redis sharding'

    def getShards(self):
        return self.client


