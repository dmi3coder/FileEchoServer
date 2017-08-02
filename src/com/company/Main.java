package com.company;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Main {
    static List<Byte> bytes;
    static File file = new File("/Users/dim3coder/Documents/Projects/FileEchoServer","backup.zip");

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8000),0);
        server.createContext("/backup",new BackupHandler());
        server.createContext("/available",new BackupAvailable());
        server.createContext("/has", httpExchange -> {
            httpExchange.sendResponseHeaders(file.exists()?200:404,0);
            httpExchange.close();
        });
        server.setExecutor(null);
        server.start();
    }

    static class BackupHandler implements HttpHandler{

        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            InputStream stream = httpExchange.getRequestBody();
            FileOutputStream outputStream = new FileOutputStream(file);
            bytes = new ArrayList<Byte>();
            byte[] buf = new byte[1024];
            while ((stream.read(buf)>0)){
                outputStream.write(buf);
            }
            outputStream.flush();
            outputStream.close();
            stream.close();
            httpExchange.sendResponseHeaders(200,0);
            httpExchange.close();
        }
    }

    static class BackupAvailable implements HttpHandler{

        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            InputStream in = new FileInputStream(file);
            ByteArrayOutputStream _out = new ByteArrayOutputStream();
            byte[] buf = new byte[2048];
            int read = 0;
            while ((read = in.read(buf)) != -1) {
                _out.write(buf, 0, read)

            httpExchange.sendResponseHeaders(200, 0);

            OutputStream out = httpExchange.getResponseBody();
            in = new ByteArrayInputStream(_out.toByteArray());
            while ((read = in.read(buf)) != -1) {
                out.write(buf, 0, read);
            }
            out.flush();
            httpExchange.close();
        }
    }
}
