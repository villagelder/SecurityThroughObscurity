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

    private static void writeDecipheredData(List<String> dList) throws IOException {

        FileWriter writer = new FileWriter("deciphered.dat");
        for (String str : dList) {
            writer.write(str + System.lineSeparator());
        }
        writer.close();

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

    public static void deciperChecksumData() {
        List<String> decipheredList = new ArrayList<>();

        try {
            List<String> checksumList = readChecksumData();

            for (String checksum : checksumList) {
                decipheredList.add(decipherRoomName(checksum));
            }

            writeDecipheredData(decipheredList);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static String decipherRoomName(String encryptedString) {
        int sectorId = getSectorID(encryptedString);
        String roomName = getRoomName(encryptedString);

        char[] charArray = roomName.toCharArray();
        char[] decipheredArray = new char[charArray.length];

        for (int i = 0; i < charArray.length; i++) {
            decipheredArray[i] = deciper(charArray[i], sectorId);
        }

        StringBuilder sb = new StringBuilder();

        for (char c : decipheredArray)
            sb.append(c);

        return sb.toString() + "-" + sectorId;
    }

    private static char deciper(char ch, int shift) {
        int min = 'a';
        int max = 'z';
        int n = 0;
        char result;

        int r = shift % 26;

        if (ch == '-') {
            return ' ';
        } else if (r == 0) {
            return ch;
        } else {

            if ((ch + r) > max) {
                n = ch + r - max + min - 1;
            } else {
                n = ch + r;
            }
        }

        result = (char) n;
        return result;
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

    public static void main(String[] args) {
        deciperChecksumData();

    }

}
