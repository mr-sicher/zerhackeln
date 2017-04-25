package parktikum.functions;

import java.util.ArrayList;

/**
 * Created by sicher on 06.04.2017.
 */
public abstract class BasicFunction {
    public static byte[] buildSendData(String inhalt, int wert, int nummer){
        ArrayList<Byte> sendBytes = new ArrayList<>();
        int inhaltLength = inhalt.length();
        int wertLength = (int)(Math.log(wert) / Math.log(128)) + 1;
        if(wert == 0) {
            wertLength = 1;
        }
        int nummerLength = (int)(Math.log(nummer) / Math.log(128)) + 1;
        if(nummer == 0){
            nummerLength = 1;
        }
        int dataLength = 1 + inhaltLength + 1 + nummerLength + 1 + wertLength + 1;
        //int length = ("" + dataLength).length()/256 + 1;
        int tmp = dataLength;
        ArrayList<Byte> tmpList =  new ArrayList<>();
        do{
            tmpList.add((byte) (tmp%128));
            tmp /= 128;
        }while(tmp > 0);
        for(int i = tmpList.size() -1 ; i >= 0; i --){
            sendBytes.add(tmpList.get(i));
        }
        sendBytes.add((byte) -1);

        for(int i = 0; i < inhaltLength; i ++){
            sendBytes.add((byte) inhalt.getBytes()[i]);
        }
        sendBytes.add((byte) -1);

        tmp = nummer;
        tmpList =  new ArrayList<>();
        do{
            tmpList.add((byte) (tmp%128));
            tmp /= 128;
        }while(tmp > 0);
        for(int i = tmpList.size() -1 ; i >= 0; i --){
            sendBytes.add(tmpList.get(i));
        }
        sendBytes.add((byte) -1);

        tmp = wert;
        tmpList =  new ArrayList<>();
        do{
            tmpList.add((byte) (tmp%128));
            tmp /= 128;
        }while(tmp > 0);
        for(int i = tmpList.size() -1 ; i >= 0; i --){
            sendBytes.add(tmpList.get(i));
        }
        sendBytes.add((byte) -1);

        //System.out.println(sendBytes);
        return convert(sendBytes);
    }

    private static byte[] convert(ArrayList<Byte> list){
        byte[] ret = new byte[list.size()];
        for(int i = 0; i < list.size(); i ++){
            ret[i] = list.get(i);
        }
        return ret;
    }

    public static int getSendData(byte[] bytes, Data data){
        int ret = 0;
        int pointer = 0;
        try {
            while (bytes[pointer] != -1) {
                ret *= 128;
                ret += bytes[pointer];
                pointer += 1;
            }
            pointer += 1;

            int str = pointer;
            while(bytes[pointer] != -1){
                pointer += 1;
            }
            data.inhalt = new String(bytes, str, pointer-str);
            pointer += 1;

            while (bytes[pointer] != -1) {
                data.nummer *= 128;
                data.nummer += bytes[pointer];
                pointer += 1;
            }
            pointer += 1;

            while (bytes[pointer] != -1) {
                data.wert *= 128;
                data.wert += bytes[pointer];
                pointer += 1;
            }

            if(pointer != ret){
                throw new IncorrectDataException(pointer + "!=" + ret);
            }
        }catch (IndexOutOfBoundsException | IncorrectDataException e){
            System.out.println(e.getMessage());
            data = null;
            return -1;
        }

        return ret;
    }
}
