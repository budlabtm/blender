package org.blab.blender.connect.vcas;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import org.blab.blender.connect.common.MessageStream;

/** VCAS MessageStream implementation. */
public class VcasMessageStream implements MessageStream {
  private final ByteBuffer buffer = ByteBuffer.allocate(128);
  private String[] sources;
  private AsynchronousSocketChannel channel;
  private Callback callback;

  @Override
  public void connect(Map<String, String> properties) throws Exception {
    sources = properties.get(VcasSourceConfiguration.SOURCES).split(",");
    channel = AsynchronousSocketChannel.open();
    channel.connect(
        new InetSocketAddress(
            properties.get(VcasSourceConfiguration.VCAS_HOST),
            Integer.parseUnsignedInt(VcasSourceConfiguration.VCAS_PORT)),
        null,
        new ConnectionHandler());
  }

  @Override
  public void disconnect() throws Exception {
    if (channel != null && channel.isOpen()) channel.close();
  }

  @Override
  public void setCallback(Callback callback) {
    this.callback = callback;
  }

  private void subscribe(String source) throws ExecutionException, InterruptedException {
    String request = String.format("name:%s|method:subscr\n", source);
    channel.write(ByteBuffer.wrap(request.getBytes())).get();
  }

  private Map<String, String> parseMessage(String message) {
    return Arrays.stream(message.split("\\|"))
        .map(s -> s.split(":"))
        .collect(Collectors.toMap(s -> s[0], s -> s[1]));
  }

  class ConnectionHandler implements CompletionHandler<Void, Void> {
    @Override
    public void completed(Void result, Void attachment) {
      callback.onConnect("Connected.");

      try {
        for (String source : sources) subscribe(source);
        channel.read(buffer, null, new MessageHandler());
      } catch (Exception e) {
        callback.onError(e);
      }
    }

    @Override
    public void failed(Throwable exc, Void attachment) {
      callback.onError(new Exception(exc));
    }
  }

  class MessageHandler implements CompletionHandler<Integer, Void> {
    @Override
    public void completed(Integer result, Void attachment) {
      try {
        Map<String, String> tokens = parseMessage(String.valueOf(buffer.asCharBuffer().array()));

        callback.onMessage(
            tokens.get("name"),
            Double.parseDouble(tokens.get("value")),
            new SimpleDateFormat("dd.MM.yyyy HH_mm_ss.SSS").parse(tokens.get("time")).getTime());
        channel.read(buffer, null, this);
      } catch (Exception e) {
        callback.onError(e);
      }
    }

    @Override
    public void failed(Throwable exc, Void attachment) {
      callback.onError(new Exception(exc));
    }
  }
}
