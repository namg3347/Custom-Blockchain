package com.redhat.Crypto;

import com.redhat.Crypto.Entities.Block;
import com.redhat.Crypto.Entities.Transaction;
import com.redhat.Crypto.Entities.TransactionOutput;
import com.redhat.Crypto.Entities.Wallet;
import com.redhat.Crypto.Util.StringUtil;


import java.security.Provider;
import java.security.Security;
import java.util.ArrayList;
import java.util.HashMap;


public class CustomChain {

    public static ArrayList<Block> blockchain = new ArrayList<Block>();
    public static int difficulty = 6;
    public static HashMap<String, TransactionOutput> UTXOs = new HashMap<>();
    public static Wallet wallet1;
    public static Wallet wallet2;
    public static float minimumTransaction = 1;


    public static void main(String[] args) {

        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        wallet1 = new Wallet();
        wallet2 = new Wallet();
        System.out.println("Private and public keys:");
        System.out.println(StringUtil.getStringFromKey(wallet1.privateKey));
        System.out.println(StringUtil.getStringFromKey(wallet1.publicKey));

        Transaction transaction = new Transaction(wallet1.publicKey, wallet2.publicKey, 5, null);

        transaction.generateSignature(wallet1.privateKey);
        System.out.println("is Verified");
        System.out.println(transaction.verifySignature());

//        Block b0 = new Block(0,"0","Hi im the first block");
//        System.out.println("Trying to Mine block 0... ");
//        b0.mineBlock(difficulty);
//        blockchain.add(b0);
//
//        if(isChainValid()) {
//            Block b1 = new Block(1,blockchain.get(blockchain.size()-1).hash,"Yo im the second block");
//            System.out.println("Trying to Mine block 1... ");
//            b1.mineBlock(difficulty);
//            blockchain.add(b1);
//        }
//
//        if(isChainValid()) {
//            Block b2 = new Block(2,blockchain.get(blockchain.size()-1).hash,"Hey im the third block");
//            System.out.println("Trying to Mine block 2... ");
//            b2.mineBlock(difficulty);
//            blockchain.add(b2);
//        }
//
//        System.out.println("\nBlockchain is Valid: " + isChainValid());


    }

    public static Boolean isChainValid() {
        Block currentBlock;
        Block previousBlock;
        String hashTarget = new String(new char[difficulty]).replace('\0', '0');

        //loop through blockchain to check hashes:
        for(int i=1; i < blockchain.size(); i++) {
            currentBlock = blockchain.get(i);
            previousBlock = blockchain.get(i-1);
            //compare registered hash and calculated hash:
            if(!currentBlock.hash.equals(currentBlock.calculateHash()) ){
                System.out.println("Current Hashes not equal");
                return false;
            }
            //compare previous hash and registered previous hash
            if(!previousBlock.hash.equals(currentBlock.prevHash) ) {
                System.out.println("Previous Hashes not equal");
                return false;
            }

            if(!currentBlock.hash.substring( 0, difficulty).equals(hashTarget)) {
                System.out.println("This block hasn't been mined");
                return false;
            }
        }
        return true;
    }
}