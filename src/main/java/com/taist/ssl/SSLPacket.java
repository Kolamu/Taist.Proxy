package com.taist.ssl;

import com.taist.helper.ByteHelper;

import java.util.ArrayList;
import java.util.List;

/**
 *  该类表示SSL连接单次接收到的数据包
 *
 *  单个数据包中可能包含多个SSL握手信息
 */
public class SSLPacket {
    private ArrayList<SSLPlainText> sslPlainTextArrayList = new ArrayList<SSLPlainText>();
    public SSLPacket(byte[] packData){
        parse(packData);
    }

    private void parse(byte[] packData) {
        int len = packData.length;
        int index = 0;
        while(index < len){
            SSLPlainText text = new SSLPlainText(ByteHelper.subarray(packData, index, len - index));
            index += text.getLength() + 5;
            sslPlainTextArrayList.add(text);
        }
    }

    public List<SSLPlainText> getRequestList(){
        return sslPlainTextArrayList;
    }

    public byte[] getBytes(){
        byte[] bts = new byte[0];
        for(SSLPlainText text : sslPlainTextArrayList){
            bts = ByteHelper.concat(bts, text.getBytes());
        }
        return bts;
    }
}
