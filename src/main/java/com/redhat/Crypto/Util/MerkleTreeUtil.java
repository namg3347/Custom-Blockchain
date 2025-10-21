package com.redhat.Crypto.Util;

import com.redhat.Crypto.Entities.Transaction;

import java.util.ArrayList;
import java.util.List;

public class MerkleTreeUtil {

    public static String generateMerkleRoot(List<Transaction> transaction) {

        if (transaction == null || transaction.isEmpty()) {
            return "";
        }
        List<String> level =  new ArrayList<>();
        for(Transaction t : transaction) {
            level.add(StringUtil.sha256(t.transactionId));
        }

        while(level.size()>1) {
            level = generateNext(level);
        }
        return level.get(0);
    }

    public static List<String> generateNext(List<String> level) {
        List<String> next = new ArrayList<>();

        int i =0;
        while(i<level.size()) {
            String left = level.get(i);
            String right;
            if(i+1<level.size()) right = level.get(i+1);
            else right = left;
            next.add(StringUtil.sha256(left+right));
            i+=2;
        }
        return next;
    }




}
