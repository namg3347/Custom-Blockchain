package com.redhat.Crypto.Entities;

import com.redhat.Crypto.CustomChain;
import com.redhat.Crypto.Util.StringUtil;

import java.security.*;
import java.util.ArrayList;

public class Transaction {
    public String transactionId;
    public PublicKey sender;
    public PublicKey reciepient;

    public float value;
    public byte[] signature;

    public ArrayList<TransactionInput> inputs = new ArrayList<>();
    public ArrayList<TransactionOutput> outputs = new ArrayList<>();

    private static int sequence =0;

    public Transaction(PublicKey from, PublicKey to, float value,  ArrayList<TransactionInput> inputs) {
        this.sender = from;
        this.reciepient = to;
        this.value = value;
        this.inputs = inputs;
    }

    public boolean processTransaction() {

        //verify sig
        if(!verifySignature()) {
            System.out.println("Signature verification failed");
            return false;
        }
        //copy all the utxo to current inputs
        for(TransactionInput i : inputs) {
            i.UTXO = CustomChain.UTXOs.get(i.transactionOutputId);
        }
        //if total in utxo is less the minimum transaction limit of the chain
        if(getInputsValue() < CustomChain.minimumTransaction) {
            System.out.println("#Transaction Inputs to small: " + getInputsValue());
            return false;
        }

        //generate the transaction output
        float leftOver = getInputsValue()-value;
        outputs.add(new TransactionOutput(this.reciepient, value, this.transactionId));
        outputs.add(new TransactionOutput(this.sender, leftOver, this.transactionId));

        //add outputs to Unspent list
        for(TransactionOutput o : outputs) {
            CustomChain.UTXOs.put(o.id , o);
        }

        //remove transaction inputs from UTXO lists as spent
        for(TransactionInput i : inputs) {
            if(i.UTXO == null) continue;
            CustomChain.UTXOs.remove(i.UTXO.id);
        }

        return true;

    }

    public void generateSignature(PrivateKey privateKey)
    {
        String data = StringUtil.getStringFromKey(sender)+StringUtil.getStringFromKey(reciepient) + Float.toString(value) + sequence;
        signature = StringUtil.eccSign(privateKey, data);
    }

    public boolean verifySignature()
    {
        String data = StringUtil.getStringFromKey(sender)+StringUtil.getStringFromKey(reciepient) + Float.toString(value) + sequence;
        return StringUtil.verifyECDSASig(sender, data, signature);
    }

    //calculate hash of the transaction to store as id
    private String calulateHash() {
        sequence++;
        return StringUtil.sha256(
                StringUtil.getStringFromKey(sender) +
                        StringUtil.getStringFromKey(reciepient) +
                        Float.toString(value) + sequence
        );
    }

    //return total sum of all the unspent transactions(UTXOs)
    public float getInputsValue() {
        float total = 0;
        for(TransactionInput i : inputs) {
            if(i.UTXO == null) continue;
            total += i.UTXO.value;
        }
        return total;
    }
    //return total sum of outputs after the transaction is done
    public float getOutputsValue() {
        float total = 0;
        for(TransactionOutput o : outputs) {
            total += o.value;
        }
        return total;
    }

}
