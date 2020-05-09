package com.company;

import java.time.chrono.MinguoDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class RoomChecker {


    public static void decipher(String encryptedString) {


    }

    private static String getChecksum(String encryptedString) {
        return encryptedString.substring(encryptedString.indexOf("[") + 1, encryptedString.indexOf("]"));

    }

    private static String getRoomName(String encryptedString) {
        return null;

    }

    private static List<String> segmentEncryption(String encryptedString) {
        String shortEnc = encryptedString.substring(0, encryptedString.indexOf("["));
        List<String> parsedList = new ArrayList<>(Arrays.asList(shortEnc.split("-")));

        return parsedList;
    }

    private static int getSectorID(String encryptedString) {
        return 0;
    }

    private static String readCharacterStream(Stream<Character> cs) {
        StringBuilder sb = new StringBuilder();

        cs.forEach(ch -> sb.append(ch));
        return sb.toString();
    }

    public static void main(String[] args) {
        System.out.println("\n\nParsed: " + segmentEncryption("aaaaa-bbb-z-y-x-123[abxyz]"));
    }

}
