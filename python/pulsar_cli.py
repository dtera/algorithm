# coding:utf-8
import sys
from time import sleep

import pulsar
from sympy import OperationNotSupported


class PulsarCli:
    def __init__(self, service_url, token=None, topic=None, subscription=None, message_listener=None,
                 batching_enabled=True, batching_max_publish_delay_ms=500, receiver_queue_size=5000):
        self.client = pulsar.Client(service_url, authentication=pulsar.AuthenticationToken(token) if token else None)
        self.subscription = subscription
        if self.subscription:
            self.consumer = self.client.subscribe(topic, subscription,
                                                  consumer_type=pulsar.ConsumerType.Shared,
                                                  message_listener=message_listener,
                                                  receiver_queue_size=receiver_queue_size)
            self.listening = True if message_listener else False
        else:
            self.producer = self.client.create_producer(topic,
                                                        compression_type=pulsar.CompressionType.ZSTD,
                                                        batching_enabled=batching_enabled,
                                                        batching_max_publish_delay_ms=batching_max_publish_delay_ms)

    def send(self, content):
        self.producer.send(content)

    def send_str(self, content: str):
        self.send(content.encode('utf-8'))

    def send_async(self, content, send_callback):
        self.producer.send_async(content, callback=send_callback)

    def send_async_str(self, content: str, send_callback):
        self.send_async(content.encode('utf-8'), send_callback)

    def listen_recv(self):
        while self.listening:
            sleep(1)

    def recv(self):
        msg = self.consumer.receive()
        try:
            # Acknowledge successful processing of the message
            self.consumer.acknowledge(msg)
        except OperationNotSupported:
            # Message failed to be processed
            self.consumer.negative_acknowledge(msg)
        return msg

    def close(self):
        if self.subscription:
            self.listening = False
        self.client.close()


def send_callback(res, msg_id):
    if res == pulsar.Result.Ok:
        print("Message [%s] sent successfully" % str(msg_id))
    else:
        print(f"Failed to send message [%s]: [%s]" % (msg_id, res))


def message_listener(consumer, msg):
    try:
        print("Received message '{}' id='{}'".format(msg.data().decode('utf-8'), msg.message_id()))
        consumer.acknowledge(msg)
    except OperationNotSupported:
        consumer.negative_acknowledge(msg)


if __name__ == "__main__":
    role = sys.argv[1] if len(sys.argv) > 1 else "producer"
    size = 20
    if role == "producer":
        pulsar_cli = PulsarCli("pulsar://pulsar.wx.com:32041", topic="persistent://test/test_ns/p_test_topic")
        for i in range(size):
            pulsar_cli.send_async_str(f'Hello-{i}', send_callback=send_callback)
            sleep(2)
    else:
        pulsar_cli = PulsarCli("pulsar://pulsar.wx.com:32041", topic="persistent://test/test_ns/p_test_topic",
                               subscription="p_test_sub"  # , message_listener=message_listener
                               )
        for i in range(size):
            msg = pulsar_cli.recv()
            print("Received message '{}' id='{}'".format(msg.data().decode('utf-8'), msg.message_id()))
