package org.nest.core.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.apache.log4j.Logger;

/**
 * 文件读取管理类
 */
public class FileManage {
    private static final int BYTESIZE = 2048;
    private static Logger logger = Logger.getLogger(FileManage.class);
    private static final String lineSeparator = "\n\r";

    /**
     * 移动文件和文件夹
     * 
     * @param strSourceFileName
     *            是指定的文件全路径名
     * @param strDestDir
     *            移动到指定的文件夹中
     * @return 如果成功true; 否则false
     */
    public static boolean moveFile(String strSourceFileName, String strDestDir) {
        File file = new File(strSourceFileName);
        return file.renameTo(new File(strDestDir));

    }

    public static String readFromFile(String filename) {
        File f = new File(filename);
        if (!f.isFile()) {
            logger.error(filename + "文件不存在");
            return null;
        }

        StringBuffer sb = new StringBuffer();
        try {
            InputStreamReader isr = new InputStreamReader(
                    new FileInputStream(f), "UTF-8");
            BufferedReader reader = new BufferedReader(isr);
            String s = null;
            while ((s = reader.readLine()) != null) {
                sb.append(s).append(lineSeparator);
            }
            reader.close();
        } catch (Exception e) {
            logger.error("读取文件" + filename + "时出错", e);
            return null;
        }
        return sb.toString();
    }

    public static byte[] readBytesFromFile(String filename) {
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        File f = new File(filename);
        if (!f.isFile())
            return null;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(f));
            int s = 0;
            while ((s = reader.read()) != -1) {
                bao.write(s);
            }
            reader.close();
        } catch (Exception e) {
            logger.error("读取文件" + filename + "时出错", e);
            return null;
        }
        return bao.toByteArray();
    }

    /**
     * 按分号截取文件，得到一组结果集
     * @param text
     *            待分析文本字符串
     * @return List 分析结果集
     */
    public static List analysisLine(String text) {
        List list = new ArrayList();
        StringTokenizer stoken = new StringTokenizer(text, ";");
        while (stoken.hasMoreTokens()) {
            list.add(stoken.nextToken());
        }
        return list;
    }

    /**
     * 按冒号截取文件，返回table表与路径的对应关系
     * @param list
     *            待分析的List结果集
     * @return Map 分析结果集
     */
    public static List analysisPath(List list) {
        Map map = null;
        List list_path = new ArrayList();
        for (int i = 0; i < list.size(); i++) {
            map = new HashMap();
            String temp[] = ((String) list.get(i)).split(":");
            map.put(temp[1], temp[0]);
            list_path.add(map);
        }
        return list_path;
    }

    /**
     * 删除单个文件
     * @param fileName
     *            要删除的文件的文件名
     * @return 单个文件删除成功返回true，否则返回false
     */
    public static boolean deleteFile(String fileName) {
        return deleteFile(new File(fileName));
    }

    /**
     * 删除单个文件
     * @param fileName
     *            要删除的文件的文件名
     * @return 单个文件删除成功返回true，否则返回false
     */
    public static boolean deleteFile(File f) {
        if (f.isDirectory()) {
            File[] fs = f.listFiles();
            for (File file : fs) {
                if (!deleteFile(file)) {
                    return false;
                }
            }
            return f.delete();
        } else {
            return f.delete();
        }
    }

    public static File file2Zip(File file, File endFile, String filename)
            throws IOException {
        ZipOutputStream zos = new ZipOutputStream(new BufferedOutputStream(
                new FileOutputStream(endFile)));
        zos.putNextEntry(new ZipEntry(filename));
        InputStream is = new FileInputStream(file);
        FileManage.copyStream(is, zos);
        return endFile;
    }

    /**
     * <h4>名称:</h4> 将文件夹压缩成zip <h4>表述:</h4> docFile：需要压缩的目录，zipFile
     * 压缩目标文件，如果压缩文件不存在则在需要压缩目录中生存压缩文件
     * @param docFile
     * @param zipFile
     * @return zipFile
     * @throws IOException
     * @throws IOException
     * @throws
     */
    public static File doc2Zip(File srcdir, File zipFile) throws IOException {
        if (!srcdir.exists()) {
            throw new RuntimeException(srcdir.getPath() + "不存在！");
        }

        FileOutputStream fos = new FileOutputStream(zipFile);
        ZipOutputStream out = new ZipOutputStream(fos);
        compress(srcdir, out, "");
        out.close();
        return zipFile;
    }

    private static void compress(File file, ZipOutputStream out, String basedir)
            throws IOException {
        /* 判断是目录还是文件 */
        if (file.isDirectory()) {
            // System.out.println("压缩：" + basedir + file.getName());
            compressDirectory(file, out, basedir);
        } else {
            // System.out.println("压缩：" + basedir + file.getName());
            compressFile(file, out, basedir);
        }
    }

    /**
     * 压缩一个目录
     * @throws IOException
     */
    private static void compressDirectory(File dir, ZipOutputStream out,
            String basedir) throws IOException {
        if (!dir.exists())
            return;
        ZipEntry entry = new ZipEntry(basedir + dir.getName() + "/");
        out.putNextEntry(entry);
        out.closeEntry();
        File[] files = dir.listFiles();
        for (int i = 0; i < files.length; i++) {
            /* 递归 */
            compress(files[i], out, basedir + dir.getName() + "/");
        }
    }

    /**
     * 压缩一个文件
     * @throws IOException
     */
    private static void compressFile(File file, ZipOutputStream out,
            String basedir) throws IOException {
        if (!file.exists()) {
            return;
        }
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(
                file));
        ZipEntry entry = new ZipEntry(basedir + file.getName());
        out.putNextEntry(entry);
        int count;
        byte data[] = new byte[BYTESIZE];
        while ((count = bis.read(data, 0, BYTESIZE)) != -1) {
            out.write(data, 0, count);
        }
        bis.close();
        out.closeEntry();
    }

    /**
     * <h4>名称:</h4> zip解压缩到指定目录 <h4>表述:</h4>
     * @param zipFile
     * @param docFile
     * @throws IOException
     * @throws
     */
    public static File zip2Doc(File zipFile, File docFile) throws IOException {
        ZipInputStream zis = new ZipInputStream(new BufferedInputStream(
                new FileInputStream(zipFile)));
        File fout = null;
        ZipEntry entry = null;
        while ((entry = zis.getNextEntry()) != null) {
            fout = new File(docFile, entry.getName());
            if (entry.isDirectory()) {
                if (!fout.exists()) {
                    fout.mkdirs();
                }
            } else {
                if (!fout.exists()) {
                    (new File(fout.getParent())).mkdirs();
                }
                BufferedOutputStream out = new BufferedOutputStream(
                        new FileOutputStream(fout));
                FileManage.copyStream(zis, out);
                out.close();
            }
        }
        zis.close();
        return docFile;
    }

    /**
     * . 拷贝数据
     * @param in
     *            输入流
     * @param out
     *            输出流
     * @throws IOException
     *             异常
     */
    public static void copyStream(InputStream in, OutputStream out)
            throws IOException {
        byte[] chunk = new byte[BYTESIZE];
        int count;

        while ((count = in.read(chunk)) >= 0) {
            out.write(chunk, 0, count);
        }
    }

    public static void main(String[] args) {
        FileManage.moveFile("D:/a1/a2", "D:/a3/a2");
    }
}
