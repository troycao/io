package com.troy.io.nio.p1;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

/**
 * @Author troy
 * @Date 2021/5/4 7:09 下午
 * @Version 1.0
 * @Desc:
 */
public class NIOServer {
    public static void main(String[] args) {
        try {
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.socket().bind(new InetSocketAddress(9000));
            Selector selector = Selector.open();
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            while (true){
                System.out.println("等待事件发生...");
                int select = selector.select();
                System.out.println("有事情发生...");
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                while (iterator.hasNext()){
                    SelectionKey selectionKey = iterator.next();
                    iterator.remove();
                    handle(selectionKey);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static void handle(SelectionKey key) throws IOException {
        if (key.isAcceptable()){
            System.out.println("有客户端连接事情发生了...");
            ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
            SocketChannel socketChannel = serverSocketChannel.accept();
            socketChannel.configureBlocking(false);
            socketChannel.register(key.selector(), SelectionKey.OP_READ);
        } else if (key.isReadable()){
            SocketChannel socketChannel = (SocketChannel) key.channel();
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            int len = socketChannel.read(buffer);
            if (len != -1){
                System.out.println("读取到客户端发送的数据：" + new String(buffer.array(), 0, len));
            }
            ByteBuffer bufferToWrite = ByteBuffer.wrap("helloclient".getBytes(StandardCharsets.UTF_8));
            socketChannel.write(bufferToWrite);
            key.interestOps(SelectionKey.OP_READ | SelectionKey.OP_WRITE);
        } else if (key.isWritable()){
            SocketChannel socketChannel = (SocketChannel) key.channel();
            System.out.println("write 事件");
            key.interestOps(SelectionKey.OP_READ);
        }
    }
}
