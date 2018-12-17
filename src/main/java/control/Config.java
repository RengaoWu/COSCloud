package control;

import java.io.IOException;
import java.util.Properties;

public class Config {
    public static String appId = "1253943763";
    public static String secretId = "AKIDyssnYArt1nU1NXHe1sbCcqsrl9PU5K0Z";
    public static String secretKey ="b4LRM6e2EIDTVO6cw52IQNAlV8nHagbL";
    public static String region = "ap-beijing";
    public static String bucket = "cos-1253943763";

    public static int initConfig() {
        Properties properties = new Properties();
        try {
            properties.load(Config.class.getClassLoader().getResourceAsStream("Config.properties"));
        } catch (IOException e) {
            return -1;
        }
        appId = properties.getProperty("appId");
        secretId = properties.getProperty("secretId");
        secretKey = properties.getProperty("secretKey");
        region = properties.getProperty("region");
        bucket = properties.getProperty("bucket");
        if (appId==null||secretId==null||secretKey==null||region==null||bucket==null){
            return -2;
        }
        return 1;
    }

    public static void setAppId(String appId) {
        Config.appId = appId;
    }

    public static void setSecretId(String secretId) {
        Config.secretId = secretId;
    }

    public static void setSecretKey(String secretKey) {
        Config.secretKey = secretKey;
    }

    public static void setRegion(String region) {
        Config.region = region;
    }

    public static void setBucket(String bucket) {
        Config.bucket = bucket;
    }
}
