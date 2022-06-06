//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.ddkj.buffalo.util;

import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Stack;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class FileUtil {
    public FileUtil() {
    }
    /**
     * 将MultipartFile保存到指定的路径下
     *
     * @param file
     *            Spring的MultipartFile对象
     * @param savePath
     *            保存路径
     * @return 保存的文件名，当返回NULL时为保存失败。
     */
    public static String save(MultipartFile file, String savePath) throws IllegalStateException, IOException {
        if (file != null && file.getSize() > 0) {
            File fileFolder = new File(savePath);
            if (!fileFolder.exists()) {
                boolean mkdirs = fileFolder.mkdirs();
            }
            File saveFile = getFile(savePath, file.getOriginalFilename());
            file.transferTo(saveFile);
            return saveFile.getName();
        }
        return null;
    }

    /**
     * 删除文件
     *
     * @param filePath
     *            文件路径
     * @return 是否删除成功：true-删除成功，false-删除失败
     */
    public static boolean delete(String filePath) {
        File file = new File(filePath);
        if (file.isFile()) {
            return file.delete();
        }
        return false;
    }
    /** InputStream转字符串*/
    public static String toString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return sb.toString();
    }

    /**
     * 写入文件,读写汉字
     */
    public static boolean writeFileChinese(String filePath,String str) throws Exception{
        File file = new File(filePath);
        try(FileWriter fw = new FileWriter(file);BufferedWriter out = new BufferedWriter(fw)){// FileWriter(file,true);表示追加文字
            if (!file.exists()) {
                boolean newFile = file.createNewFile();
            }
            out.write(str);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static boolean writeFile(String filePath,String str){
        File f = new File(filePath);
        try(FileOutputStream out = new FileOutputStream(f)){
            byte[] b = str.getBytes(StandardCharsets.UTF_8);//获取字符串转换的字节
            out.write(b);
            //out.close();//try with无需关闭流
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static boolean writeFile(String filePath, String str, Charset charset){
        File f = new File(filePath);
        try(FileOutputStream out = new FileOutputStream(f)){
            byte[] b = str.getBytes(charset);
            out.write(b);
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static String readFile(String filePath){
        File file = new File(filePath);
        StringBuilder sb = new StringBuilder();
        try(BufferedReader reader = new BufferedReader(new FileReader(file))){
            String temp;
            while ((temp = reader.readLine()) != null) {
                sb.append(temp);
            }
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    public static byte[] toBytes(String str){
        return str.getBytes();
    }
    public static byte[] toBytes(String str, Charset charset){
        return str.getBytes(charset);
    }
    public static String toString(byte[] bytes){
        return new String(bytes);
    }
    public static String toString(byte[] bytes, Charset charset){
        return new String(bytes,charset);
    }


    private static File getFile(String savePath, String originalFilename) {
        String fileName = System.currentTimeMillis() + "_" + originalFilename;
        File file = new File(savePath + fileName);
        if (file.exists()) {
            return getFile(savePath, originalFilename);
        }
        return file;
    }

    public static boolean exists(String path) {
        return (new File(path)).exists();
    }

    public static boolean existsFile(String path) {
        File file = new File(path);
        return file.exists() && file.isFile();
    }

    public static boolean existsAny(String... paths) {
        return Arrays.stream(paths).anyMatch((path) -> {
            return (new File(path)).exists();
        });
    }

    public static void deleteIfExists(File file) throws IOException {
        if (file.exists()) {
            if (file.isFile()) {
                if (!file.delete()) {
                    throw new IOException("Delete file failure,path:" + file.getAbsolutePath());
                }
            } else {
                File[] files = file.listFiles();
                if (files != null && files.length > 0) {
                    File[] var2 = files;
                    int var3 = files.length;

                    for(int var4 = 0; var4 < var3; ++var4) {
                        File temp = var2[var4];
                        deleteIfExists(temp);
                    }
                }

                if (!file.delete()) {
                    throw new IOException("Delete file failure,path:" + file.getAbsolutePath());
                }
            }
        }

    }

    public static void deleteIfExists(String path) throws IOException {
        deleteIfExists(new File(path));
    }

    public static File createFile(String path) throws IOException {
        return createFile(path, false);
    }

    public static File createDir(String path) throws IOException {
        return createDir(path, false);
    }

    public static File createFile(String path, boolean isHidden) throws IOException {
        File file = createFileSmart(path);
        if (OsUtil.isWindows()) {
            Files.setAttribute(file.toPath(), "dos:hidden", isHidden);
        }

        return file;
    }

    public static File createDir(String path, boolean isHidden) throws IOException {
        File file = new File(path);
        deleteIfExists(file);
        File newFile = new File(path);
        newFile.mkdir();
        if (OsUtil.isWindows()) {
            Files.setAttribute(newFile.toPath(), "dos:hidden", isHidden);
        }

        return file;
    }

    public static long getFileSize(String path) {
        File file = new File(path);
        if (file.exists()) {
            if (file.isFile()) {
                return file.length();
            } else {
                long size = 0L;
                File[] files = file.listFiles();
                if (files != null && files.length > 0) {
                    File[] var5 = files;
                    int var6 = files.length;

                    for(int var7 = 0; var7 < var6; ++var7) {
                        File temp = var5[var7];
                        if (temp.isFile()) {
                            size += temp.length();
                        }
                    }
                }

                return size;
            }
        } else {
            return 0L;
        }
    }

    public static File createFileSmart(String path) throws IOException {
        try {
            File file = new File(path);
            if (file.exists()) {
                file.delete();
                file.createNewFile();
            } else {
                createDirSmart(file.getParent());
                file.createNewFile();
            }

            return file;
        } catch (IOException var2) {
            throw new IOException("createFileSmart=" + path, var2);
        }
    }

    public static File createDirSmart(String path) throws IOException {
        try {
            File file = new File(path);
            if (!file.exists()) {
                Stack<File> stack = new Stack();

                for(File temp = new File(path); temp != null; temp = temp.getParentFile()) {
                    stack.push(temp);
                }

                while(stack.size() > 0) {
                    File dir = (File)stack.pop();
                    if (!dir.exists()) {
                        dir.mkdir();
                    }
                }
            }

            return file;
        } catch (Exception var5) {
            throw new IOException("createDirSmart=" + path, var5);
        }
    }

    public static long getDiskFreeSize(String path) {
        File file = new File(path);
        return file.getFreeSpace();
    }

    public static void unmap(MappedByteBuffer mappedBuffer) throws IOException {
        try {
            Class<?> clazz = Class.forName("sun.nio.ch.FileChannelImpl");
            Method m = clazz.getDeclaredMethod("unmap", MappedByteBuffer.class);
            m.setAccessible(true);
            m.invoke(clazz, mappedBuffer);
        } catch (Exception var3) {
            throw new IOException("LargeMappedByteBuffer close", var3);
        }
    }

    public static String getFileNameNoSuffix(String fileName) {
        int index = fileName.lastIndexOf(".");
        return index != -1 ? fileName.substring(0, index) : fileName;
    }

    public static void initFile(String path, boolean isHidden) throws IOException {
        initFile(path, (InputStream)null, isHidden);
    }

    public static void initFile(String path, InputStream input, boolean isHidden) throws IOException {
        RandomAccessFile raf;
        Throwable var4;
        if (exists(path)) {
            raf = new RandomAccessFile(path, "rws");
            var4 = null;

            try {
                raf.setLength(0L);
            } catch (Throwable var39) {
                var4 = var39;
                throw var39;
            } finally {
                if (var4 != null) {
                    try {
                        raf.close();
                    } catch (Throwable var37) {
                        var4.addSuppressed(var37);
                    }
                } else {
                    raf.close();
                }

            }
        } else {
            createFile(path, isHidden);
        }

        if (input != null) {
            try {
                raf = new RandomAccessFile(path, "rws");
                var4 = null;

                try {
                    byte[] bts = new byte[8192];

                    int len;
                    while((len = input.read(bts)) != -1) {
                        raf.write(bts, 0, len);
                    }
                } catch (Throwable var41) {
                    var4 = var41;
                    throw var41;
                } finally {
                    if (raf != null) {
                        if (var4 != null) {
                            try {
                                raf.close();
                            } catch (Throwable var38) {
                                var4.addSuppressed(var38);
                            }
                        } else {
                            raf.close();
                        }
                    }

                }
            } finally {
                input.close();
            }
        }

    }

    public static boolean canWrite(String path) {
        File file = new File(path);
        File test;
        if (file.isFile()) {
            test = new File(file.getParent() + File.separator + UUID.randomUUID().toString() + ".test");
        } else {
            test = new File(file.getPath() + File.separator + UUID.randomUUID().toString() + ".test");
        }

        try {
            test.createNewFile();
            test.delete();
            return true;
        } catch (IOException var4) {
            return false;
        }
    }

    public static void unzip(String zipPath, String toPath, String... unzipFile) throws IOException {
        ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(zipPath));
        Throwable var4 = null;

        try {
            toPath = toPath == null ? (new File(zipPath)).getParent() : toPath;

            ZipEntry entry;
            while((entry = zipInputStream.getNextEntry()) != null) {
                String entryName = entry.getName();
                if (entry.isDirectory() || unzipFile != null && unzipFile.length > 0 && Arrays.stream(unzipFile).noneMatch((filex) -> {
                    return entryName.equalsIgnoreCase(filex);
                })) {
                    zipInputStream.closeEntry();
                } else {
                    File file = createFileSmart(toPath + File.separator + entryName);
                    FileOutputStream outputStream = new FileOutputStream(file);
                    Throwable var9 = null;

                    try {
                        byte[] bts = new byte[8192];

                        int len;
                        while((len = zipInputStream.read(bts)) != -1) {
                            outputStream.write(bts, 0, len);
                        }
                    } catch (Throwable var33) {
                        var9 = var33;
                        throw var33;
                    } finally {
                        if (outputStream != null) {
                            if (var9 != null) {
                                try {
                                    outputStream.close();
                                } catch (Throwable var32) {
                                    var9.addSuppressed(var32);
                                }
                            } else {
                                outputStream.close();
                            }
                        }

                    }
                }
            }
        } catch (Throwable var35) {
            var4 = var35;
            throw var35;
        } finally {
            if (zipInputStream != null) {
                if (var4 != null) {
                    try {
                        zipInputStream.close();
                    } catch (Throwable var31) {
                        var4.addSuppressed(var31);
                    }
                } else {
                    zipInputStream.close();
                }
            }

        }

    }

    public static String renameIfExists(String path) {
        File file = new File(path);
        if (file.exists() && file.isFile()) {
            int index = file.getName().lastIndexOf(".");
            String name = file.getName().substring(0, index);
            String suffix = index == -1 ? "" : file.getName().substring(index);
            int i = 1;

            String newName;
            do {
                newName = name + "(" + i + ")" + suffix;
                ++i;
            } while(existsFile(file.getParent() + File.separator + newName));

            return newName;
        } else {
            return file.getName();
        }
    }

    public static void createFileWithSparse(String filePath, long length) throws IOException {
        Path path = Paths.get(filePath);

        try {
            Files.deleteIfExists(path);
            SeekableByteChannel channel = Files.newByteChannel(path, StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE, StandardOpenOption.SPARSE);
            Throwable var5 = null;

            try {
                channel.position(length - 1L);
                channel.write(ByteBuffer.wrap(new byte[]{0}));
            } catch (Throwable var15) {
                var5 = var15;
                throw var15;
            } finally {
                if (channel != null) {
                    if (var5 != null) {
                        try {
                            channel.close();
                        } catch (Throwable var14) {
                            var5.addSuppressed(var14);
                        }
                    } else {
                        channel.close();
                    }
                }

            }

        } catch (IOException var17) {
            throw new IOException("create spares file fail,path:" + filePath + " length:" + length, var17);
        }
    }

    public static void createFileWithDefault(String filePath, long length) throws IOException {
        Path path = Paths.get(filePath);

        try {
            Files.deleteIfExists(path);
            RandomAccessFile randomAccessFile = new RandomAccessFile(filePath, "rw");
            Throwable var5 = null;

            try {
                randomAccessFile.setLength(length);
            } catch (Throwable var15) {
                var5 = var15;
                throw var15;
            } finally {
                if (randomAccessFile != null) {
                    if (var5 != null) {
                        try {
                            randomAccessFile.close();
                        } catch (Throwable var14) {
                            var5.addSuppressed(var14);
                        }
                    } else {
                        randomAccessFile.close();
                    }
                }

            }

        } catch (IOException var17) {
            throw new IOException("create spares file fail,path:" + filePath + " length:" + length, var17);
        }
    }

    public static String getSystemFileType(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            file = file.getParentFile();
        }

        return Files.getFileStore(file.toPath()).type();
    }
}
