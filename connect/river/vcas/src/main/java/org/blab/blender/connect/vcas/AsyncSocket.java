package org.blab.blender.connect.vcas;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

public class AsyncSocket implements Socket {
  private InetSocketAddress lastConnectionAddress;
  private AsynchronousSocketChannel channel;
  private ConnectionHandler cHandler;
  private MessageHandler mHandler;
  private ByteBuffer mBuffer, rBuffer;
  private Callback callback;
  private boolean isConnected;
  private char endOfMessage;

  public AsyncSocket() {
    cHandler = new ConnectionHandler();
    mHandler = new MessageHandler();
    mBuffer = ByteBuffer.allocate(512);
    rBuffer = ByteBuffer.allocate(512);
    isConnected = false;
  }

  @Override
  public void connect(InetSocketAddress address, char endOfMessage) {
    this.endOfMessage = endOfMessage;
    lastConnectionAddress = address;

    if (isConnected())
      disconnect();

    try {
      channel = AsynchronousSocketChannel.open();
      channel.connect(address, null, cHandler);
    } catch (Exception e) {
      if (callback != null)
        callback.onError(e);
    }
  }

  @Override
  public void disconnect() {
    isConnected = false;

    if (channel != null)
      try {
        channel.close();
      } catch (Exception ignored) {}
  }

  @Override
  public boolean isConnected() {
    return isConnected;
  }

  @Override
  public boolean send(String message) {
    if (!isConnected())
      return false;

    try {
      byte[] msg = message.getBytes();
      return channel.write(ByteBuffer.wrap(msg)).get().equals(msg.length);
    } catch (Exception e) {
      if (callback != null)
        callback.onError(e);

      return false;
    }
  }

  @Override
  public void setCallback(Callback callback) {
    this.callback = callback;
  }

  class ConnectionHandler implements CompletionHandler<Void, Void> {
    @Override
    public void completed(Void result, Void attachment) {
      isConnected = true;

      channel.read(mBuffer.clear(), null, mHandler);

      if (callback != null)
        callback.onConnected();
    }

    @Override
    public void failed(Throwable exc, Void attachment) {
      isConnected = false;
      connect(lastConnectionAddress, endOfMessage);
    }
  }

  class MessageHandler implements CompletionHandler<Integer, Void> {
    @Override
    public void completed(Integer result, Void attachment) {
      // TODO Implement
    }

    @Override
    public void failed(Throwable exc, Void attachment) {
      isConnected = false;
      connect(lastConnectionAddress, endOfMessage);
    }
  }
}
