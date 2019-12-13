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
        BigInteger x1 = new BigInteger(one,16);
        BigInteger x2 = new BigInteger(two,16);
        return x1.xor(x2).toByteArray();
    }
    static byte[] fixedXOR(byte[] one, byte[] two){
        BigInteger x1 = new BigInteger(one);
        BigInteger x2 = new BigInteger(two);
        return x1.xor(x2).toByteArray();
    }
    static byte[] singleByteXOR(String hex){
        byte[] message = new BigInteger(hex,16).toByteArray();
        byte[] cypher = new byte[message.length];
        //System.out.println(new String(message));
        byte[] result  = {};
        int highscore = 0;
        for(byte i=Byte.MIN_VALUE;i<Byte.MAX_VALUE;i++){
            int score = 0;
            boolean whitespace = true;
            Arrays.fill(cypher,i);
            byte [] maybe = Crypto.fixedXOR(hex,new BigInteger(cypher).toString(16));
            String s = new String(maybe);
            for(char j :s.toCharArray()){
                if(Character.isWhitespace(j)&&!whitespace){
                    score ++;
                    whitespace = true;
                }
                else if(Character.isLetterOrDigit(j)){
                    whitespace = false;
                    score++;
                }
                else score--;
            }
            if(score>highscore) {
                highscore=score;
                result = maybe;
            }
        }
        return result;
    }
    static byte[] singleByteXOR(byte[] message){
        //byte[] message = new BigInteger(hex,16).toByteArray();
        byte[] cypher = new byte[message.length];
        //System.out.println(new String(message));
        byte[] result  = {};
        int highscore = Integer.MIN_VALUE;
        for(byte i=Byte.MIN_VALUE;i<Byte.MAX_VALUE;i++){
            Arrays.fill(cypher,i);
            byte [] maybe = Crypto.fixedXOR(message,cypher);
            String s = new String(maybe);
            int score = score(s);
            System.out.println(score);
            if(score>highscore) {
                highscore=score;
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
            else score -=100;
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
    static byte[] key(){
        Scanner in;
        String s="";
        int keysize=0;
        float minDist=-1;
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
        for(int j=0;j<=20;j+=5){
            for(int i = 2; i<=40;i++){
                float dist = hammingDistance(Arrays.copyOfRange(file,j,j+i),Arrays.copyOfRange(file,j+i,j+i*2));
                float temp = dist/i;
                if(minDist==-1 || temp<minDist){
                    minDist=temp;
                    keysize=i;
                }
                //System.out.println("Hamming Distance: "+dist+"\t"+"Normaliszed: "+temp+"\t"+"I: "+i+"\t"+"Keysize: "+keysize);
            }
        }
        for(int i=0;i<keysize;i++){
            transposeFile.put(i,new ArrayList<Byte>());
        }
        for(int i = 0; i<file.length;i++){
            transposeFile.get(i%keysize).add(file[i]);
        }
        byte[][] keys = new byte[keysize][];
        for(int i:transposeFile.keySet()){
            ArrayList<Byte> c = transposeFile.get(i);
            byte[] chunk = new byte[c.size()];
            for(int j =0;j<c.size();j++)
                chunk[j]=c.get(i).byteValue();
            chunk = singleByteXOR(chunk);
            System.out.println(new String(chunk));
            keys[i] = chunk;
            //System.out.println(new String(keys[i]));
        }
        for(int i = 0; i<file.length;i++){
            finalFile[i]=keys[i%keysize][i/keysize];
        }
        return finalFile;
    }
}