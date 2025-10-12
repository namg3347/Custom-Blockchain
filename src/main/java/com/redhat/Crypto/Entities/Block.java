package com.redhat.Crypto.Entities;

import com.redhat.Crypto.Util.StringUtil;

import java.math.BigInteger;
import java.time.LocalDateTime;

public class Block {

    public int index;
    public String hash;
    public String prevHash;
    public String payLoad;
    public LocalDateTime timeStamp;
    public BigInteger nonce;

    public Block(int index,String prevHash, String payLoad) {
        this.index = index;
        this.payLoad = payLoad;
        this.timeStamp = LocalDateTime.now();
        this.prevHash = prevHash;
        this.hash = calculateHash();
        this.nonce = BigInteger.ZERO;
    }

    public String calculateHash() {
        return StringUtil.sha256(
                prevHash
                +timeStamp.toString()
                +payLoad
                +index
                +nonce
        );
    }

    public void mineBlock(int difficulty) {
        String target = new String(new char[difficulty]).replace('\0', '0');
        while(!hash.substring(0, difficulty).equals(target)) {
            nonce = nonce.add(BigInteger.valueOf(1));
            hash = calculateHash();
        }

        System.out.println("Block Mined with hash: "+hash);
    }

}
