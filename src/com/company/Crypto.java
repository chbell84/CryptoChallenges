package com.company;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class Crypto {

    //Set 1 Challenge 1
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

    //Set 1 Challenge 2
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

    //Set 1 Challenge 3
    static byte[] singleByteXOR(String hex){
        return singleByteXOR(new BigInteger(hex,16).toByteArray());
    }
    static byte[] singleByteXOR(byte[] message){
        byte[] cypher = new byte[message.length];
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

    //Set 1 Challenge 4
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

    //Set 1 Challenge 5
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
        int count =0;
        for(byte i:fixedXOR(a,b)){
            for(int j=0;j<7;j++)
                if((i >> j)%2==1){
                    count++;
                }
        }
        return count;
    }
    static int hammingDistance(String a, String b){
        return hammingDistance(a.getBytes(),b.getBytes());
    }
/*
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
*/
    static int[] keySize(byte[] file){
        int[] keysize= new int[3];
        Arrays.fill(keysize,0);
        boolean full = false;
        float[] minDist=new float[keysize.length];
        Arrays.fill(minDist, -1);
        for(int i = 2; i<=40;i++){
            byte[] r1 = Arrays.copyOfRange(file,0,0+i), r2=Arrays.copyOfRange(file,i,i*2), r3=Arrays.copyOfRange(file,i*2,i*3),r4=Arrays.copyOfRange(file,i*3,i*4);
            float dist = hammingDistance(r1,r2) + hammingDistance(r2, r3) + hammingDistance(r3,r4);
            float normalizedDist = dist/(i*3);
            for(int j=0;j<keysize.length;j++)
            {
                if(minDist[j]==-1 || normalizedDist<minDist[j]&&full){
                    minDist[j]=normalizedDist;
                    keysize[j]=i;
                    if(j==keysize.length-1) full=true;
                    break;
                }
            }
        }return keysize;
    }

    //Set 1 Challenge 6
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

    //Set 1 Challenge 7
    static byte[] aesECBDecrypt(){
        Scanner in;
        String s="";
        byte[] output  = new byte[]{};
        try {
            in = new Scanner(new File("7.txt"));
            while(in.hasNext()){
                s+=in.nextLine();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Base64.Decoder d  = Base64.getDecoder();
        byte[] message = d.decode(s);
        try {
            Cipher c = Cipher.getInstance("AES/ECB/NoPadding");
            c.init(Cipher.DECRYPT_MODE,new SecretKeySpec("YELLOW SUBMARINE".getBytes(), "AES"));
            output = c.doFinal(message);
        } catch (Exception e) {
            System.out.println("there was and error: "+e.toString());
        }
        return output;
    }

    //Set 1 Challenge 8
    static String aesECBFinder(){
        Scanner in;
        try {
            in = new Scanner(new File("8.txt"));
            while(in.hasNext()){
                String s=in.nextLine();
                if(isECB(s)) return s;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }
    static boolean isECB(String s){
        for(int i=0;i<s.length()/16;i++){
            String pattern = s.substring(16*i,16*(i+1));
            String field = s.substring(0,16*i)+s.substring(16*(i+1));
            if(field.contains(pattern)) return true;
        }
        return false;
    }

}