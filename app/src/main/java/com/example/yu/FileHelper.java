package com.example.yu;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.util.List;

public class FileHelper {

    private final Context context;


//    private final File internalDirectory;
//    private final File externalFilesDir;

    private static final String TAG = "FileHelper";
    private static boolean Return_Status = false;

    public FileHelper(Context context) {
        this.context = context;
        //外部目录
//        externalFilesDir = context.getExternalFilesDir(null);
        //内部目录
//        internalDirectory = context.getFilesDir();
    }

    // 写入文本到文件

    public void writeToFile(String fileName, String data,Boolean isAppend) {
        getInternalFile(fileName);
        FileOutputStream fos = null;
        try {
            if(isAppend){
                fos = context.openFileOutput(fileName, Context.MODE_APPEND);
                //追加
            }else {
                fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
                //覆盖
            }
            fos.write(data.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public boolean writeCategoriesToFile(String fileName,DataWatcher dataWatcher,Boolean isAppend,List<DataWatcher> dataWatchers) {
//        List<DataWatcher> dataWatchers = new ArrayList<>(); // 假设有多个 DataWatcher 对象
        // 获取缓存文件的完整路径
        File fos = getInternalFile(fileName);
//        true追加
        try (FileOutputStream fileOut = new FileOutputStream(fos,isAppend);
             ObjectOutputStream out = new ObjectOutputStream(fileOut)) {
            // 将 DataWatcher 对象写入文件
            if (dataWatchers != null) {
                out.writeObject(dataWatchers);
            }
            else if(dataWatcher != null){
                out.writeObject(dataWatcher);
            }else {
                ShowToast.show(context,"空对象写入失败");
            }
            ShowToast.show(context,"写入成功");
            return MyApplication.STATUS_TRUE;
        } catch (IOException e) {
            ShowToast.show(context,"写入失败" + e.getMessage());
            Log.i("FileHelper","写入失败" + e.getMessage());
            e.printStackTrace();
            return MyApplication.STATUS_FALSE;
        }
    }

    public  List<DataWatcher> readCategoriesToFile(String fileName){
        File fos  = getInternalFile(fileName);
        if (fos.exists()) {
            try (FileInputStream fileIn = new FileInputStream(fos);
                 ObjectInputStream in = new ObjectInputStream(fileIn)) {
                // 从文件中读取 对象
                //DataWatcher dataWatcher = (DataWatcher) in.readObject();
                Object obj = in.readObject();
                if (obj instanceof List) {
                    List<DataWatcher> dataWatcherList = (List<DataWatcher>) obj;
                    return dataWatcherList;
                } else {
                    // 处理类型不匹配的情况
                    return null;
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    // 从文件读取文本
    public String readFromFile(String fileName) {
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader reader = null;
        File file = getInternalFile(fileName);
        try {
            //InputStream inputStream = context.openFileInput(fileNamePath);
            //InputStream inputStream = new FileInputStream(file);
            InputStream inputStream = Files.newInputStream(file.toPath());
            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
            WriteToLog.writeLogToFile(context,TAG+"读取文件失败"+e.getMessage(),true);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return stringBuilder.toString();
    }

    public  File getInternalFile(String fileName) {
        File file = null;
        try {
            File internalDir = context.getFilesDir();
            // 解析文件名和目录路径
            String[] pathSegments = fileName.split("/");
            String actualFileName = pathSegments[pathSegments.length - 1];
            String relativeDirPath = fileName.substring(0, fileName.length() - actualFileName.length());

            // 默认为应用内部存储根目录
            File targetDir = internalDir;

            if (!relativeDirPath.isEmpty()) {
                targetDir = new File(internalDir, relativeDirPath);
                if (!targetDir.exists()) {
                    Return_Status = targetDir.mkdirs();
                }
            }

            File targetFile = new File(targetDir, actualFileName);

            if (!targetFile.exists()) {
                boolean created = targetFile.createNewFile();
                if (created) {
                    Log.i("FileHelper", fileName+"文件创建成功：" + fileName);
                    ShowToast.show(context, fileName+"文件创建成功");
                } else {
                    Log.i("FileHelper", fileName+"创建失败：" + fileName);
                    //ShowToast.show(context, fileName+"创建失败");
                }
            } else {
                //Log.i("FileHelper", fileName+"文件已存在：" + fileName);
                //ShowToast.show(context, fileName+"文件已存在");

            }

            file = targetFile;
        } catch (IOException e) {
            Log.e("FileHelper", fileName+"创建目录或文件时出现错误：" + e.getMessage());
            ShowToast.show(context, fileName+"创建失败");
        }

        return file;
    }


    // 递归删除目录

    protected boolean deleteInternalDirs() {
        File internalDir = context.getFilesDir();
        return deleteRecursive(internalDir);
    }

    private boolean deleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory == null) {
            return false;
        }

        if (fileOrDirectory.isDirectory()) {
            File[] files = fileOrDirectory.listFiles();
            if (files != null) {
                for (File child : files) {
                    if (!deleteRecursive(child)) {
                        return false;
                    }
                }
            }
        }

        return fileOrDirectory.delete() || !fileOrDirectory.exists();
    }

    // 递归删除子目录中的文件

    protected boolean deleteInternalFiles() {
        File internalDir = context.getFilesDir();
        return deleteFilesRecursive(internalDir);
    }

    private boolean deleteFilesRecursive(File directory) {
        if (directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        // 递归删除子目录中的文件,不删除目录
                        if (!deleteFilesRecursive(file)) {
                            return false;
                            // 删除子目录中的文件失败，返回 false
                        }
                    } else {
                        // 删除文件
                        if (!file.delete()) {
                            return false;
                            // 删除文件失败，返回 false
                        }
                    }
                }
            }
        }
        return true;
        // 默认情况下，返回 true
    }



}




//        fileHelper = new FileHelper(this);
//
//                // 写入数据到文件
//                String dataToWrite = "Hello, FileHelper!";
//                fileHelper.writeToFile("my_file.txt", dataToWrite);
//
//                // 从文件读取数据
//                String readData = fileHelper.readFromFile("my_file.txt");
//                Log.d("FileContent", readData); // 打印读取的数据
