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
            if(Character.isWhitespace(j)){
                if(!whitespace){
                    score ++;
                    whitespace = true;
                    frequency = new HashMap<>();
                }
                else score--;
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
    static int[] keySize(byte[] file){
        int[] keysize= new int[]{0,0,0};
        int keyIndex=0;
        float[] minDist=new float[]{-1,-1,-1};
        //for(int j=0;j<=20;j+=5){
        for(int i = 2; i<=40;i++){
            float dist = hammingDistance(Arrays.copyOfRange(file,0,0+i),Arrays.copyOfRange(file,i,i*2));// +
                    //hammingDistance(Arrays.copyOfRange(file,i*2,i*3),Arrays.copyOfRange(file,i*3,i*4));
            float normalizedDist = dist/(i);
            if(minDist[0]==-1 || normalizedDist<minDist[0]){
                minDist[0]=normalizedDist;
                keysize[0]=i;
            }
            else if(minDist[1]==-1 || normalizedDist<minDist[1]){
                minDist[1]=normalizedDist;
                keysize[1]=i;
            }
            else if(minDist[2]==-1 || normalizedDist<minDist[2]){
                minDist[2]=normalizedDist;
                keysize[2]=i;
            }
            //System.out.println("Hamming Distance: "+dist+"\t"+"Normaliszed: "+normalizedDist+"\t"+"I: "+i+"\t"+"Keysize: "+keysize);
        }return keysize;
    }
    static byte[] key(){
        Scanner in;
        String s="";
        try {
            in = new Scanner(new File("6.txt"));
            while(in.hasNext()){
                s+=in.nextLine();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Base64.Decoder dec = Base64.getDecoder();
        byte[] file = dec.decode(s);
        byte[] result={},temp;
        int highscore=Integer.MIN_VALUE,score;
        int[] keysize = keySize(file);
        //int[] keysize = new int[]{2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40};

        //try to decrypt at each keysize in the array,
        //score the resulting strings and return the best one
        for(int key:keysize){
            System.out.println("Key: "+key);
            temp = decrypter(file, key);
            score = score(new String(temp));
            if(score>highscore){
                highscore=score;
                result=temp;
            }
        }
        return result;
    }
    static byte[] decrypter(byte[] file, int keysize){
        double chunkLength = Math.ceil(file.length/keysize);
        byte[][] keyChunks = new byte[(int) chunkLength][keysize];
        byte[][] transposedChunks = new byte[keysize][(int) chunkLength];
        byte[][] decryptedChunks = new byte[keysize][(int) chunkLength];
        int currValue = 0;

        //broke the file into keySize length chunks
        for(int i=0;i<keyChunks.length;i++){
            for(int j=0;j<keyChunks[i].length;j++){
                if (currValue < file.length) {
                    keyChunks[i][j] = file[currValue];
                    currValue += 1;
                } else {
                    keyChunks[i][j] = 0;
                }
            }
        }

        //Transposes those chunks into a new array
        for (int j = 0; j < keyChunks[0].length; j++) {
            for(int i=0;i<keyChunks.length;i++) {
                transposedChunks[j][i] = keyChunks[i][j];
            }
        }

        //run SingleByteXOR on each chunk
        for(int i=0;i<transposedChunks.length;i++){
            decryptedChunks[i]=singleByteXOR(transposedChunks[i]);
        }
        byte[] decryptedFile = new byte[file.length];
        int index =0;
        for(int i=0;i<decryptedChunks[0].length;i++)
            for(int j =0;j<decryptedChunks.length;j++){
                if(index<decryptedFile.length){
                    decryptedFile[index]=decryptedChunks[j][i];
                    index++;
                }
            }
        return decryptedFile;
    }
}