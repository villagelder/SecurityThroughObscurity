package com.company;

import java.net.URL;
import java.util.*;
import java.net.*;
import java.io.*;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class RoomChecker {

    private static String getChecksum(String encryptedString) throws StringIndexOutOfBoundsException {
        return encryptedString.substring(encryptedString.indexOf("[") + 1, encryptedString.indexOf("]"));

    }

    private static Map<Character, Integer> characterCountMap(String encryptedString) {
        Map<Character, Integer> alphaMap = new HashMap<>();

        String roomName = getRoomName(encryptedString);

        for (int i = 0; i < roomName.length(); i++) {

            if (alphaMap.containsKey(roomName.charAt(i))) {
                int tmp = alphaMap.get(roomName.charAt(i));
                alphaMap.put(roomName.charAt(i), tmp + 1);
            } else {
                alphaMap.put(roomName.charAt(i), 1);
            }

        }

        alphaMap.remove('-');

        return alphaMap;
    }

    //didn't work probably because of user login requirement
//    private static void readInput() throws MalformedURLException {
//        List<String> checksumList = new ArrayList<>();
//
//        URL adventofcode = new URL("https://adventofcode.com/2016/day/4/input");
//
//        try {
//            BufferedReader br = new BufferedReader(new InputStreamReader(adventofcode.openStream()));
//
//            String inputLine;
//            while ((inputLine = br.readLine()) != null)
//                checksumList.add(inputLine);
//
//            br.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        for (String str : checksumList)
//            System.out.print(str);
//    }

    private static List<String> readChecksumData() throws IOException {
        InputStream input = RoomChecker.class.getResourceAsStream("checksum.dat");
        BufferedReader br = new BufferedReader(new InputStreamReader(input));

        List<String> checksumList = new ArrayList<>();

        String line = null;
        while ((line = br.readLine()) != null) {
            checksumList.add(line);
            if (line.equalsIgnoreCase("quit")) {
                break;
            }
        }
        return checksumList;
    }

    private static LinkedList<Map.Entry<Character, Integer>> sortMapByValuesDesc(Map<Character, Integer> unsortedMap) {

        LinkedList<Map.Entry<Character, Integer>> sortedList = new LinkedList<>(unsortedMap.entrySet());
        Comparator<Map.Entry<Character, Integer>> comparator = Comparator.comparing(Map.Entry::getValue);
        Collections.sort(sortedList, comparator.reversed());

        return sortedList;
    }

    private static LinkedList<Map.Entry<Character, Integer>> sortTiesByKeysDesc
            (LinkedList<Map.Entry<Character, Integer>> semiSortedList) {
        TreeMap<Character, Integer> tmap = new TreeMap<>();
        LinkedList<Map.Entry<Character, Integer>> checksumSortedList = new LinkedList<>();

        for (int i = 0; i < semiSortedList.size(); i++) {
            Map.Entry<Character, Integer> listEntry = semiSortedList.get(i);
            Map.Entry<Character, Integer> nextEntry = null;

            if (i + 1 < semiSortedList.size())
                nextEntry = semiSortedList.get(i + 1);

            if (tmap.isEmpty() || tmap.values().contains(listEntry.getValue())) {
                tmap.put(listEntry.getKey(), listEntry.getValue());

            } else {
                tmap = new TreeMap<>();
                tmap.put(listEntry.getKey(), listEntry.getValue());
            }

            if (nextEntry == null || listEntry.getValue() != nextEntry.getValue()) {
                for (Map.Entry<Character, Integer> entry : tmap.entrySet())
                    checksumSortedList.add(entry);
            }
        }

        return checksumSortedList;
    }

    private static LinkedList<Map.Entry<Character, Integer>> countAndSort(String encryptedString) {
        Map<Character, Integer> map = characterCountMap(encryptedString);
        LinkedList<Map.Entry<Character, Integer>> semiSortedList = sortMapByValuesDesc(map);
        LinkedList<Map.Entry<Character, Integer>> checksumSortedList = sortTiesByKeysDesc(semiSortedList);

        return checksumSortedList;
    }

    public static boolean isRoomReal(String encryptedString) {
        String checksum = getChecksum(encryptedString);
        Character[] characterArray = checksum.chars()
                .mapToObj(ch -> (char) ch)
                .toArray(Character[]::new);

        LinkedList<Map.Entry<Character, Integer>> checksumSortedList = countAndSort(encryptedString);

        System.out.print("\nentry list");
        for (Map.Entry<Character, Integer> entry : checksumSortedList)
            System.out.print("\n" + entry.getKey());

        System.out.print("\n\nchar Array");

        for (Character c : characterArray)
            System.out.print("\n" + c);

        int i = 0;
        for (Character ch : characterArray) {

            if (!ch.equals(checksumSortedList.get(i).getKey()))
                return false;
            i++;
        }

        return true;
    }

    public static int sumRealRoomIds(List<String> encryptionList) {
        int sum = 0;
        for (String encryptedString : encryptionList) {
            if (isRoomReal(encryptedString)) {
                sum += getSectorID(encryptedString);
            }

        }

        return sum;
    }

    public static int getChecksumDataAndSumRealIDs() throws IOException {
        return sumRealRoomIds(readChecksumData());
    }

    private static String getRoomName(String encryptedString) {
        StringBuilder sb = new StringBuilder();
        List<String> encryptionSegmentList = segmentEncryption(encryptedString);
        encryptionSegmentList.remove(encryptionSegmentList.size() - 1);

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
        return new ArrayList<String>(Arrays.asList(shortEnc.split("-")));
    }

    private static int getSectorID(String encryptedString) throws NumberFormatException {
        List<String> encryptionSegmentList = segmentEncryption(encryptedString);
        int id = Integer.parseInt(encryptionSegmentList.get(encryptionSegmentList.size() - 1));

        return id;
    }

    public static void main(String[] args) throws IOException {
        System.out.println("Total: " + getChecksumDataAndSumRealIDs());

    }

}
