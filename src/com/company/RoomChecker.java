package com.company;

import java.time.chrono.MinguoDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

public class RoomChecker {


    public static void decipher(String encryptedString) {


    }

    private static String getChecksum(String encryptedString) {
        return encryptedString.substring(encryptedString.indexOf("[") + 1, encryptedString.indexOf("]"));

    }

    private static String getRoomName(String encryptedString) {
        List<String> encryptionSegmentList = segmentEncryption(encryptedString);
        encryptionSegmentList.remove(encryptionSegmentList.size() - 1);
        StringBuilder sb = new StringBuilder();

        Iterator it = encryptionSegmentList.iterator();

        while (it.hasNext()) {
            sb.append((String) it.next());
            if (it.hasNext())
                sb.append("-");
        }

        return sb.toString();
    }

    private static List<String> segmentEncryption(String encryptedString) {
        String shortEnc = encryptedString.substring(0, encryptedString.indexOf("["));
        return new ArrayList<>(Arrays.asList(shortEnc.split("-")));

    }

    private static int getSectorID(String encryptedString) throws NumberFormatException {
        List<String> encryptionSegmentList = segmentEncryption(encryptedString);
        int id = Integer.parseInt(encryptionSegmentList.get(encryptionSegmentList.size() - 1));

        return id;
    }

    public static void main(String[] args) {
        System.out.println("\n\nRoom Name: " + getRoomName("a-b-c-d-e-f-g-h-987[abcde]"));
    }

}
