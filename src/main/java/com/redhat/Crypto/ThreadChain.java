package com.redhat.Crypto;

import com.redhat.Crypto.Entities.Block;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class ThreadChain {

    public static ArrayList<Block> blockchain = new ArrayList<Block>();
    public static int difficulty = 5;
    private static AtomicBoolean blockMined = new AtomicBoolean(false);


    public static class ChainThread extends Thread {

        public String miner;
        public Block block;

        public ChainThread(String miner, Block block) {
            this.miner = miner;
            this.block = block;
        }

        @Override
        public void run() {
            System.out.println(miner + " started mining...");
            String target = new String(new char[difficulty]).replace('\0', '0');

            while (!blockMined.get()) {
                block.nonce.add(BigInteger.ONE);
                block.hash = block.calculateHash();

                if (block.hash.substring(0, difficulty).equals(target)) {
                    if (blockMined.compareAndSet(false, true)) {
                        blockchain.add(block);
                        System.out.println("⛏️ " + miner + " mined the block: " + block.hash);
                    }
                    return;
                }
            }
        }
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


    public static void main(String[] args) {
        ThreadChain chain = new ThreadChain();

        Block currentBlock = new Block(0,
                blockchain.size()==0?"0":blockchain.get(blockchain.size()-1).hash,
                "Miners testing");
        Thread miner1 = new ChainThread("miner1",currentBlock);
    }
}
