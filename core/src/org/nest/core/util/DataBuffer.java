package org.nest.core.util;

import java.util.LinkedList;

public class DataBuffer {
    // 数据缓冲中指定的数据类型
    Class _clazz = null;

    // 数据缓冲
    LinkedList _buffer = new LinkedList();

    // 缓冲是否关闭
    boolean _closeed = false;

    // 缓冲等待超时时间为1分钟
    long _overtime = 600000;

    /**
     * 向堆栈中装入一个对象
     * 
     * @param o
     */
    public synchronized void push(Object o) {
        // 缓冲已经关闭,不能在加入数据
        if (isClose()) {
            throw new RuntimeException("DataBuffer closed");
        }

        // 装入数据缓冲的对象不是制定的类型
        if (_clazz != null && o != null
                && !_clazz.isAssignableFrom(o.getClass())) {
            throw new RuntimeException("Class of PushObject is " + o.getClass()
                    + " not " + _clazz);
        }

        // 防止同时存取对象
        synchronized (_buffer) {
            _buffer.addLast(o);
        }
    }

    /**
     * 从堆栈中弹出一个数据
     * 
     * @return
     */
    public synchronized Object pop() {
        if (_buffer.isEmpty()) {

            return null;
            // throw new RuntimeException("This DataBuffer is empty");
        } else {
            // 防止同时存取对象
            synchronized (_buffer) {
                return _buffer.removeFirst();
            }
        }
    }

    /**
     * 清空堆栈
     * 
     */
    public synchronized void clear() {
        // 防止同时存取对象
        synchronized (_buffer) {
            this._buffer.clear();
        }
    }

    /**
     * 关闭缓冲
     * 
     */
    public synchronized void close() {
        _closeed = true;
    }

    /**
     * 判断该缓冲是否被关闭
     * 
     * @return
     */
    public boolean isClose() {
        return _closeed;
    }

    /**
     * 判断数据缓冲是否情况
     * 
     * @return
     */
    public boolean isClear() {
        return false;
    }

    /**
     * 返回堆栈中的对象个数
     * 
     * @return
     */
    public synchronized int size() {
        return _buffer.size();
    }

    /**
     * 判断数据缓冲中是否还有数据 只有当数据缓冲对象已经关闭,并且缓冲中已经没有数据时才返回true,否则会等待其他线程继续向缓冲中追加数据
     * 
     * @return
     */
    public boolean hasData() {
        if (isClose()) {
            // 如果数据缓冲已经关闭，则返回缓冲的状态
            return !_buffer.isEmpty();
        } else {
            // 如果数据缓冲已经布安离，则等待填充线程填充数据
            long start = System.currentTimeMillis();
            // 在超时时间内等待
            while (_buffer.isEmpty()) {
                waitFill(1);
                if (_overtime > 0
                        && (System.currentTimeMillis() - start) > _overtime) {
                    // 数据状态超时，关闭缓冲
                    close();
                    break;
                }
            }
            return !_buffer.isEmpty();
        }

    }

    /**
     * 延时线程
     * 
     * @param m
     */
    private synchronized void waitFill(long m) {
        try {
            synchronized (this) {
                wait(m);
            }
        } catch (InterruptedException e) {
        }
    }

    public static void main(String[] arg) {
        DataBuffer list = new DataBuffer();
        TestWriteBufferThread t = new TestWriteBufferThread(list);
        t.start();

        Object clock = new Object();
        // 等待4秒
        try {
            synchronized (clock) {
                clock.wait(1000);
            }
        } catch (InterruptedException e) {
        }

        for (; list.hasData();) {
            System.out.println("读取: " + list.pop());
        }

    }

    /**
     * 设置堆栈等待超时时间
     * 
     * @param i
     */
    public void setOverTime(long i) {
        this._overtime = i;

    }
}

class TestWriteBufferThread extends java.lang.Thread {

    DataBuffer list = null;

    public TestWriteBufferThread(DataBuffer list) {
        this.list = list;
    }

    @Override
    public void run() {
        // 每隔100毫秒写入一个数字
        for (int i = 0; i < 50; i++) {
            try {
                synchronized (this) {
                    wait(100);
                }
            } catch (InterruptedException e) {
            }
            list.push("" + i);
            System.out.println("写入：" + i);
        }

        list.close();
    }
}
