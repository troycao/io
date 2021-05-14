package com.troy.io.nio.p1;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;

public class NioServerSocket {

    public static void main(String[] args) throws IOException{
        try {
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.socket().bind(new InetSocketAddress(9000));
            Selector selector = Selector.open();
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            while (true){
                System.out.println("等待事件发生。。");
                // 轮询监听channel里的key，select是阻塞的，accept()也是阻塞的
                int select = selector.select();
                System.out.println("有事件发生了。。");
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                if (iterator.hasNext()){
                    SelectionKey selectionKey = iterator.next();
                    iterator.remove();
                    handler(selectionKey);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void handler(SelectionKey selectionKey) throws IOException{
        if (selectionKey.isAcceptable()){
            System.out.println("有客户端连接事件发生了。。");
            ServerSocketChannel channel = (ServerSocketChannel) selectionKey.channel();
            SocketChannel socketChannel = channel.accept();
            socketChannel.configureBlocking(false);
            socketChannel.register(selectionKey.selector(), SelectionKey.OP_READ);
        } else if (selectionKey.isReadable()){
            System.out.println("有客户端数据可读事件发生了。。");
            SocketChannel channel = (SocketChannel) selectionKey.channel();
            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
            int len = channel.read(byteBuffer);
            if (len != -1){
                System.out.println("读取到客户端发送的数据：" + new String(byteBuffer.array(), 0, len));
            }
            ByteBuffer bufferToWrite = ByteBuffer.wrap("HelloClient".getBytes());
            channel.write(bufferToWrite);
            selectionKey.interestOps(SelectionKey.OP_READ | SelectionKey.OP_WRITE);
        } else if (selectionKey.isWritable()){
            System.out.println("有客户端数据可写事件发生了。。");
            SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
            selectionKey.interestOps(SelectionKey.OP_READ);
        }
    }
}
