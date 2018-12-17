package control;

import com.fasterxml.jackson.databind.ser.std.FileSerializer;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.http.HttpMethodName;
import com.qcloud.cos.model.COSObjectSummary;
import com.qcloud.cos.model.GeneratePresignedUrlRequest;
import com.qcloud.cos.model.ObjectListing;
import com.qcloud.cos.model.UploadResult;
import com.qcloud.cos.region.Region;
import com.qcloud.cos.transfer.Download;
import com.qcloud.cos.transfer.TransferManager;


import java.io.File;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Client {
    private static Client client;
    private COSClient cosClient;
    private ExecutorService threadPool;
    private TransferManager transferManager;

    private Client() {
        COSCredentials credentials = new BasicCOSCredentials(Config.secretId, Config.secretKey);
        ClientConfig clientConfig = new ClientConfig(new Region(Config.region));
        cosClient = new COSClient(credentials, clientConfig);
        threadPool = Executors.newFixedThreadPool(32);
        transferManager = new TransferManager(cosClient, threadPool);
    }

    public static Client getClient() {
        if (client == null) {
            client = new Client();
        }
        return client;
    }

    // 增加文件
    public void putObject(String[] files) {
        for (int i = 1; i < files.length; i++) {
            File file = new File(files[i]);
            // 如果是文件直接上传
            // 如果是目录，设置KEY
            if (file.isDirectory()) {
                putDirectory(file.getParent(), file.listFiles());
            } else {
                putFile(file.getName(), file);
            }
        }
        listObject("main");
    }

    public void putDirectory(String parent, File[] files) {
        for (File f : files) {
            if (f.isDirectory()) putDirectory(parent, f.listFiles());
            else {
                String key = f.getAbsolutePath().replace(parent, "");
                putFile(key, f);
            }
        }
    }

    public void putFile(final String key, final File file) {
        new Runnable() {
            public void run() {
                try {
                    UploadResult result = transferManager.upload(Config.bucket, key, file).waitForUploadResult();
                    System.out.println("RESULT_OK\t" + result.getKey() + "\t" + getUrl(result.getKey()));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.run();
    }

    public URL getUrl(String key) {
        GeneratePresignedUrlRequest req = new GeneratePresignedUrlRequest(Config.bucket, key, HttpMethodName.GET);
        URL url = cosClient.generatePresignedUrl(req);
        return url;
    }

    // 查询
    public void listObject(String prefix) {
        ObjectListing objectListing = cosClient.listObjects(Config.bucket, prefix);
        List<COSObjectSummary> summaries = objectListing.getObjectSummaries();

        System.out.println("文件列表：");
        for (COSObjectSummary summary : summaries) {
//            System.out.println("KEY:\t" + summary.getKey());
//            System.out.println("SIZE:\t" + summary.getSize());
//            System.out.println("MODIFIED:\t" + summary.getLastModified());
//            System.out.println("URL:\t" + getUrl(summary.getKey()));
//            System.out.println();
            downloadFile(summary.getKey(), "/home/rengao/文档/测试COS/download");
        }
    }

    // 删除
    public void delObjects(String[] key) {
        for (int i = 1; i < key.length; i++) {
            cosClient.deleteObject(Config.bucket, key[i]);
        }
    }

    public void getFiles(String[] keys, String path) {
        for (String key : keys) {
            downloadFile(key, path);
        }
    }

    public void downloadFile(final String key, final String path) {
        new Runnable() {
            public void run() {
                //String[] splitKey = key.split(File.separator);
                //String filename = splitKey[splitKey.length - 1]; // 获取文件的名字

                File localFile = null;
                if (path.toCharArray()[path.toCharArray().length - 1] != File.separatorChar) {
                    //localFile = new File(path + File.separator + filename);
                    localFile = new File(path + File.separator + key);
                } else {
                    //localFile = new File(path + filename);
                    localFile = new File(path + key);

                }
                Download download = transferManager.download(Config.bucket, key, localFile);
                while (!download.isDone()) {
                    System.out.println(localFile.getName() + ":" + download.getProgress().getPercentTransferred());
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                System.out.println(localFile.getAbsolutePath() + "下载完成");
            }
        }.run();
    }
}
