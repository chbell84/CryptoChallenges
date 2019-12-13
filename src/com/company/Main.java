package com.company;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Base64;

public class Main {

    public static void main(String[] args) {
	// write your code here
        //Set 1 Challenge 1
        byte [] b = Crypto.hexTo64("49276d206b696c6c696e6720796f757220627261696e206c696b65206120706f69736f6e6f7573206d757368726f6f6d");
        Base64.Decoder dec = Base64.getDecoder();
        System.out.println(new String(Crypto.hexTo64("49276d206b696c6c696e6720796f757220627261696e206c696b65206120706f69736f6e6f7573206d757368726f6f6d")));
        System.out.println(new String(dec.decode(b)));
        //Set 1 Challenge 2
        System.out.println(new BigInteger(Crypto.fixedXOR("1c0111001f010100061a024b53535009181c","686974207468652062756c6c277320657965")).toString(16));
        System.out.println(new String(Crypto.fixedXOR("1c0111001f010100061a024b53535009181c","686974207468652062756c6c277320657965")));
        //Set 1 Challenge 3
        System.out.println(new String(Crypto.singleByteXOR("1b37373331363f78151b7f2b783431333d78397828372d363c78373e783a393b3736")));
        //Set 1 Challenge 4
        System.out.println(new String(Crypto.findSingleCharXOR("4.txt")));
        //Set 1 Challenge 5
        System.out.println("Burning 'em, if you ain't quick and nimble\n" +
                "I go crazy when I hear a cymbal");
        System.out.println("0"+new BigInteger(Crypto.repeatingKeyXOR("Burning 'em, if you ain't quick and nimble\n" +
                "I go crazy when I hear a cymbal")).toString(16));
        System.out.println("0b3637272a2b2e63622c2e69692a23693a2a3c6324202d623d63343c2a26226324272765272a282b2f20430a652e2c652a3124333a653e2b2027630c692b20283165286326302e27282f");
        //Set 1 Challenge 6
        System.out.println(new String(Crypto.key()));
        //System.out.println(Crypto.hammingDistance("this is a test","wokka wokka!!!"));

    }
}
