package util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

// --put files                       return key-url
// --get path/file bucketName key
// --delBucket   bucketName key
// --newBucket   newBucketName key   # bucket shouldn't be too much
// --isBucket   # return false/true
// --getRegion  # return bucket region
// --listBucket # return all bucket
// --listObject # return all files in the bucket

// --setACL     #
// --getACL     #

// CORS

//bucket life cycle

public class OptionCommand {
    private static OptionCommand optionCommand;
    public static List<String> list = new ArrayList<String>();

    private OptionCommand(){
        list.add("--put");
        list.add("--get");
        list.add("--listObject");
        list.add("--isBucket");
        list.add("--getRegion");
        list.add("--listBucket");
        list.add("--delBucket");
        list.add("--newBucket");
    }

    public static OptionCommand getOptionCommand(){
        if(optionCommand==null){
            optionCommand = new OptionCommand();
        }
        return optionCommand;
    }
    public static int checkOption(String[] strings){
        getOptionCommand();
        return list.indexOf(strings[0]);
    }

}
