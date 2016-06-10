package org.nest.core.util;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;

import org.apache.log4j.Logger;

public class RandomGUID {

    private static Logger logger = Logger.getLogger(RandomGUID.class);

    // guid缓冲
    private static DataBuffer _buffer = new DataBuffer();

    // 缓冲区限制
    private static long _size = 1000;

    // 工作状态
    private static boolean running = false;

    // 线称锁
    private static Object _clock = new Object();

    private static GUIDCreator _creator = null;

    public String createGUID() {
        return RandomGUID.geneGuid();
    }

    public synchronized static String geneGuid() {

        String guid = null;
        try {
            guid = (String) _buffer.pop();
            if (guid == null)
                RandomGUID.startCreator();
        } catch (RuntimeException e) {
            RandomGUID.startCreator();
        }

        while (guid == null) {
            waitFill(1);

            try {
                guid = (String) _buffer.pop();
                if (guid == null) {
                    RandomGUID.startCreator();
                }

            } catch (RuntimeException e) {
                RandomGUID.startCreator();
            }
        }

        if (_buffer.size() < _size / 2) {
            RandomGUID.startCreator();
        }

        return guid;
    }

    public long getCreatorStart() {
        // TODO 自动生成方法存根
        return 0;
    }

    public long getGUIDCount() {
        return _buffer.size();
    }

    public long getGUIDPoolSize() {
        return _size;
    }

    public void setGUIDPoolSize(long size) {
        RandomGUID._size = size;
    }

    public void start() {
        RandomGUID.startCreator();
    }

    public static void startCreator() {
        if (_creator == null) {
            _creator = new GUIDCreator();
            _creator.setDaemon(true);
            _creator.start();
        } else if (!_creator.isAlive()) {
            // 如果线成已经结束，则重新创建一个新线程
            _creator = null;
            startCreator();
        } else {
            synchronized (_creator) {
                // 继续想缓冲中生成GUID
                _creator.notifyAll();
            }
        }
    }

    public void stop() {
        RandomGUID.stopCreator();
    }

    public static void stopCreator() {
        RandomGUID._size = 0;
    }

    public String getObjectName() {
        return "IntegrationServer.Util:name=GUIDCreator";
    }

    public void init() {
        RandomGUID.startCreator();
    }

    public void restart() {
        RandomGUID._buffer.clear();
        RandomGUID.startCreator();
    }

    /**
     * 延时线程
     * 
     * @param m
     */
    private synchronized static void waitFill(long m) {
        try {
            synchronized (_clock) {
                _clock.wait(m);
            }
        } catch (InterruptedException e) {
        }
    }

    private static class GUIDCreator extends Thread {

        private final SecureRandom mySecureRand;

        private final Random myRand;

        private String s_id;

        private String valueBeforeMD5 = "";

        private String valueAfterMD5 = "";

        // 异常退出标识
        private boolean err_exit_flag = false;

        public GUIDCreator() {
            mySecureRand = new SecureRandom();
            long secureInitializer = mySecureRand.nextLong();
            myRand = new Random(secureInitializer);
            try {
                s_id = InetAddress.getLocalHost().toString();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            while (!err_exit_flag) {

                while (RandomGUID._buffer.size() < RandomGUID._size) {
                    RandomGUID._buffer.push(getRandomGUID(true));
                }

                // 这里暂停线程，等待下次启动
                synchronized (this) {
                    try {
                        this.wait();
                    } catch (InterruptedException e) {
                        logger.error("GUID Creator 线程异常", e);
                        err_exit_flag = true;
                    }
                }
            }
        }

        /*
         * Method to generate the random RandomGUID
         */
        private String getRandomGUID(boolean secure) {
            MessageDigest md5 = null;
            StringBuffer sbValueBeforeMD5 = new StringBuffer();

            try {
                md5 = MessageDigest.getInstance("MD5");
            } catch (NoSuchAlgorithmException e) {
                System.out.println("Error:   " + e);
            }

            try {
                long time = System.currentTimeMillis();
                long rand = 0;

                if (secure) {
                    rand = mySecureRand.nextLong();
                } else {
                    rand = myRand.nextLong();
                }

                // This StringBuffer can be a long as you need; the MD5
                // hash will always return 128 bits. You can change
                // the seed to include anything you want here.
                // You could even stream a file through the MD5 making
                // the odds of guessing it at least as great as that
                // of guessing the contents of the file!
                sbValueBeforeMD5.append(s_id);
                sbValueBeforeMD5.append(":");
                sbValueBeforeMD5.append(Long.toString(time));
                sbValueBeforeMD5.append(":");
                sbValueBeforeMD5.append(Long.toString(rand));

                valueBeforeMD5 = sbValueBeforeMD5.toString();
                md5.update(valueBeforeMD5.getBytes());

                byte[] array = md5.digest();
                StringBuffer sb = new StringBuffer();
                for (int j = 0; j < array.length; ++j) {
                    int b = array[j] & 0xFF;
                    if (b < 0x10)
                        sb.append('0');
                    sb.append(Integer.toHexString(b));
                }

                valueAfterMD5 = sb.toString();
                return valueAfterMD5;

            } catch (Exception e) {
                logger.error("Error:" + e);
            }
            return null;
        }

    }

    /**
     * @param args
     */
    public static void main(String[] args) {

        for (int i = 0; i < 1000000; i++) {
            System.out.println(i + ":" + RandomGUID.geneGuid() + ":"
                    + RandomGUID._buffer.size());
        }

    }
}
