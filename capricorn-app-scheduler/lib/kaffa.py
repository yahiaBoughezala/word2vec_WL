
from pykafka import KafkaClient
import Queue
from lib.log import MyLogger

class KafkaApi():

    def __init__(self, brokes, topic):
        self.kafkaClient = KafkaClient(hosts=brokes)
        self.topics = self.kafkaClient.topics
        self.my_topic = self.topics[topic]
        self.logger = MyLogger.getLogger()

    def send_msg(self, msg, async=False):
        if async:
            self._send_asynchronous(msg)
        else:
            self._send_sync(msg)

    def _send_asynchronous(self, messages):
        with self.my_topic.get_producer(delivery_reports=True) as producer:
            producer.produce(messages)
            try:
                msg, exc = producer.get_delivery_report(block=False)
                if exc is not None:
                    print 'Failed to delivery msg {}:{}'.format(
                        msg.partition_key, repr(exc)
                    )
                    self.logger.error('Failed to delivery msg {}:{}'.format(
                        msg.partition_key, repr(exc)
                    ))
                else:
                    print 'Successfuly deliveryed msg {}'.format(
                        msg.partition_key
                    )
                    self.logger.info('Successfuly deliveryed msg {}'.format(
                        msg.partition_key
                    ))
            except Queue.Empty:
                return



    def _send_sync(self, message):
        '''

        Exception when send message, failed

        :param message:
        :return:
        '''
        producer = self.my_topic.get_producer()
        try:
            producer.produce(message)
        except Exception, ex:
            print 'Except: failed to send kafka msg %s' % msg
            raise Exception, ex

