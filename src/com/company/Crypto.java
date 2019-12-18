package com.company;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
import java.util.*;

public class Crypto {
    static String hexTo64String(String hex){
        BigInteger x  = new BigInteger(hex,16);
        Base64.Encoder e = Base64.getEncoder();
        return e.encodeToString(x.toByteArray());
    }
    static byte[] hexTo64(String hex){
        BigInteger x  = new BigInteger(hex,16);
        Base64.Encoder e = Base64.getEncoder();
        return e.encode(x.toByteArray());
    }
    static byte[] fixedXOR(String one, String two){
        byte[] x1 = new BigInteger(one,16).toByteArray();
        byte[] x2 = new BigInteger(two,16).toByteArray();
        return fixedXOR(x1,x2);
    }
    static byte[] fixedXOR(byte[] x1, byte[] x2){
        byte[] result = new byte[x1.length];
        for(int i=0;i<x1.length;i++){
            try {
                result[i]= (byte) (x1[i] ^ x2[i]);
            }
            catch (IndexOutOfBoundsException e){
                System.out.println("Buffers not equal length");
            }
        }
        return result;
    }
    static byte[] singleByteXOR(String hex){
        return singleByteXOR(new BigInteger(hex,16).toByteArray());
    }
    static byte[] singleByteXOR(byte[] message){
        byte[] cypher = new byte[message.length];
        //System.out.println(new String(message));
        byte[] result  = {}, maybe = {};
        int highScore = Integer.MIN_VALUE, score =0;
        for(byte i=Byte.MIN_VALUE;i<Byte.MAX_VALUE;i++){
            Arrays.fill(cypher,i);
            maybe = Crypto.fixedXOR(message,cypher);
            String s = new String(maybe);
            score = score(s);
            if(score>highScore) {
                highScore=score;
                result = maybe;
            }
        }
        //System.out.println(new String(message));
        return result;
    }
    static int score(String s){
        int score = 0;
        boolean whitespace = true;
        HashMap<Character, Integer> frequency = new HashMap<>();
        for(char j :s.toCharArray()){
            if(Character.isWhitespace(j)&&!whitespace){
                score ++;
                whitespace = true;
                frequency = new HashMap<>();
            }
            else if(Character.isLetterOrDigit(j)){
                if(frequency.containsKey(j)){
                    if(frequency.get(j)>frequency.keySet().size()){
                        score --;
                    }
                    else{
                        score++;
                    }
                    frequency.put(j, frequency.get(j)+1);
                }
                else{
                    frequency.put(j,1);
                    score++;
                }
                whitespace = false;
            }
            else score -=10;
        }
        return score;
    }
    static byte[] findSingleCharXOR(String file){
        Scanner in;
        String s, best;
        byte[] b = new byte[]{}, t;
        int score  = 0;
        try {
            in = new Scanner(new File(file));
            while(in.hasNext()){
                s=in.nextLine();
                t=singleByteXOR(s);
                s=new String(t);
                int temp = score(s);
                if(temp>score){
                    score = temp;
                    b=t;
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return b;
    }
    static byte[] repeatingKeyXOR(String s){
        byte[] source = s.getBytes();
        byte[] xor = new byte[s.length()];
        Arrays.fill(xor,(byte)'I');
        for(int i =0;i<xor.length;i++){
            if(i%3==0) xor[i]=(byte)'I';
            else if(i%3==1) xor[i]=(byte)'C';
            else if(i%3==2) xor[i]=(byte)'E';
        }
        byte [] result = fixedXOR(source,xor);
        return result;
    }
    static int hammingDistance(byte[] a, byte[] b){
        //a=hexTo64(new BigInteger(a).toString(16));
        //b=hexTo64(new BigInteger(b).toString(16));
        return hammingDistance(new String(a), new String(b));
    }
    static int hammingDistance(String a, String b){
        a = new BigInteger(a.getBytes()).toString(2);
        b = new BigInteger(b.getBytes()).toString(2);
        int dis = Math.abs(a.length()-b.length());
        for(int i=0;i<Math.min(a.length(),b.length());i++){
            if(a.toCharArray()[i]!=b.toCharArray()[i]) dis++;
        }
        return dis;
    }
    static void printFileBytes(byte[] something, int limit) {
        for (int i=0; i < limit; i++) {
            System.out.print(something[i]);
            System.out.print(",");
        }
        System.out.println();
    }
    static void printArrayOfBytes(byte[][] arrayofbytes, int rows) {
        int currRow = 0;
        for(int i=0;i<arrayofbytes.length;i++){
            if (currRow >= rows) {
                return;
            }
            for(int j=0;j<arrayofbytes[0].length;j++){

                System.out.print(arrayofbytes[i][j]);
                System.out.print(", ");

            }
            currRow += 1;
            System.out.println();
        }
    }
    static int keySize(byte[] file){
        int keysize=0;
        float minDist=-1;
        //for(int j=0;j<=20;j+=5){
        for(int i = 2; i<=40;i++){
            float dist = hammingDistance(Arrays.copyOfRange(file,0,0+i),Arrays.copyOfRange(file,i,i*2));
            float dist2 = hammingDistance(Arrays.copyOfRange(file,i*2,i*3),Arrays.copyOfRange(file,i*3,i*4));
            float normalizedDist = (dist+dist2)/(i*2);
            //float normalizedDist = dist/i;
            if(minDist==-1 || normalizedDist<minDist){
                minDist=normalizedDist;
                keysize=i;
            }
            System.out.println("Hamming Distance: "+dist+"\t"+"Normaliszed: "+normalizedDist+"\t"+"I: "+i+"\t"+"Keysize: "+keysize);
        }return keysize;
    }
    static byte[] key(){
        Scanner in;
        String s="";
        //int keysize=0;
        HashMap<Integer, ArrayList<Byte>> transposeFile = new HashMap<>();
        try {
            in = new Scanner(new File("6.txt"));
            while(in.hasNext()){
                s+=in.nextLine();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        //System.out.println(s);
        Base64.Decoder dec = Base64.getDecoder();
        byte[] file = dec.decode(s);
        byte[] finalFile = new byte[file.length];

        //System.out.println("== fileBytes == ");
        //printFileBytes(file, 10);
        int keysize = keySize(file);
        //}
        double chunkLength = Math.ceil(file.length/keysize);

        //System.out.println("chunkLength: "+chunkLength+"\nKeySize: "+keysize);
        byte[][] keyChunks = new byte[(int) chunkLength][keysize];
        byte[][] transposedChunks = new byte[keysize][(int) chunkLength];
        byte[][] decryptedChunks = new byte[keysize][(int) chunkLength];
        // System.out.println("Original File:\n"+new String(Arrays.copyOfRange(file,0,30)));
        int currValue = 0;
        //broke the file into keySize length chunks
        for(int i=0;i<keyChunks.length;i++){
            //System.out.println("i: "+i+".length: "+keyChunks.length);
            for(int j=0;j<keyChunks[i].length;j++){
                if (currValue < file.length) {
                    // System.out.println("value : " + file[currValue]);
                    keyChunks[i][j] = file[currValue];
                    currValue += 1;
                } else {
                    keyChunks[i][j] = 0;
                }
            }
//            System.out.println("KeyChunks["+i+"]:"+new String(keyChunks[i]));
        }

        //System.out.println("== keyChunks ==");
        //printArrayOfBytes(keyChunks, 5);
        //System.out.println(keyChunks);

        //Transposes those chunks into a new array
        for (int j = 0; j < keyChunks[0].length; j++) {
            for(int i=0;i<keyChunks.length;i++) {
                //System.out.println("i: "+i+".length: "+keyChunks.length);
                //System.out.println("i: "+i+" j :"+j);
                transposedChunks[j][i] = keyChunks[i][j];
            }
        }

        //System.out.println("== transposedChunks ==");
        //printArrayOfBytes(transposedChunks, 5);
        //run SingleByteXOR on each chunk
        for(int i=0;i<transposedChunks.length;i++){
            decryptedChunks[i]=singleByteXOR(transposedChunks[i]);
        }
        byte[] decryptedFile = new byte[file.length];
        int index =0;
        //loopy:
        for(int i=0;i<decryptedChunks[0].length;i++)
            for(int j =0;j<decryptedChunks.length;j++){
                if(index<decryptedFile.length){
                    decryptedFile[index]=decryptedChunks[j][i];
                    index++;
                }
            }
        //System.out.println("== decrypted bytes ==");
        //printFileBytes(decryptedFile, 10);
        //System.out.println("== decryptedChunks ==");
        //printArrayOfBytes(decryptedChunks, 5);
        //System.out.println("Decrypted File:\n"+new String(decryptedFile));
        return decryptedFile;
    }
}