# socket-mq-bridge
 This application acts as a bridge interface between a socket client and a MQ application.
 This application open a socket server and waits for client on one end, while connects to IBM Websphere MQ queues on other end.
 It is a multithreaded application and implements non-blocking transactions.
 Only one socket connection is expected at a time.
