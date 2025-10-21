package com.redhat.Crypto.Entities;

import com.redhat.Crypto.Util.MerkleTreeUtil;
import com.redhat.Crypto.Util.StringUtil;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;

public class Block {

    public int index;
    public String hash;
    public String prevHash;
    public String merkleRoot;
    public ArrayList<Transaction> transactions = new ArrayList<>();
    public long timeStamp;
    public BigInteger nonce = BigInteger.ZERO;

    public Block(String prevHash) {
        this.prevHash = prevHash;
        this.timeStamp = new Date().getTime();

        this.hash = calculateHash(); //Making sure we do this after we set the other values.
    }

    public String calculateHash() {
        return StringUtil.sha256(
                prevHash +
                        index+
                        timeStamp +
                        nonce.toString()+
                        merkleRoot
        );
    }

    public void mineBlock(int difficulty) {
        merkleRoot = MerkleTreeUtil.generateMerkleRoot(transactions);
        String target = StringUtil.getDifficultyString(difficulty);
        while(!hash.substring( 0, difficulty).equals(target)) {
            this.nonce = this.nonce.add(BigInteger.ONE);
            hash = calculateHash();
        }
        System.out.println("Block Mined!!! : " + hash);
    }

    public boolean addTransaction(Transaction transaction) {
        //process transaction and check if valid, unless block is genesis block then ignore.
        if(transaction == null) return false;
        if(!prevHash.equals("0")) {
            if(!transaction.processTransaction()) {
                System.out.println("Transaction failed to process. Discarded.");
                return false;
            }
        }
        transactions.add(transaction);
        System.out.println("Transaction Successfully added to Block");
        return true;
    }

}
