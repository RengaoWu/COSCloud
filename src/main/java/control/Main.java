package control;

import util.Constant;
import util.OptionCommand;

public class Main {

    private static Client client;
    public static void main(String[] strings) {
        System.out.println(System.currentTimeMillis());
        if (!login()) return;
        client = Client.getClient();

        int option = OptionCommand.checkOption(strings);

        switch (option){
            case -1:
                System.out.println("不合法的选项："+strings[0]);
                return;

            // 上传多个文件夹或者文件，返回每个文件的KEY，URL
            case 0:
                client.putObject(strings);
                return;
            case 1:
                client.listObject(strings[1]);
                return;
            case 2:
                client.delObjects(strings);

        }
        System.out.println(System.currentTimeMillis());
    }

    public static boolean login() {
        switch (Config.initConfig()) {
            case Constant.ERROR_NOT_CONFIG:
                System.out.println("Config file not found");
                return false;
            case Constant.ERROR_NULL_CONFIG:
                System.out.println("Config file contain null setting");
                return false;
            default:
                return true;
        }
    }

}
