package com.github.android.bestpath;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 * To work on unit tests, switch_sound the Test Artifact in the Build Variants view.
 */
public class StringTest {

    private final List<Integer> list1=Arrays.asList(6,333,158,0,7,0,9,2,0,21,0,9);
    private final String string1="LV1{6,9};LV2{333,2};LV3{158,0};LV4{0,21};LV5{7,0};LV6{0,9}";
    private final List<Integer> list2=Arrays.asList(1,2,5,7,9999,12,18,80,230,21,11110,9);
    private final String string2="LV1{1,18};LV2{2,80};LV3{5,230};LV4{7,21};LV5{9999,11110};LV6{12,9}";
    @Test
    public void test_parseGameRecordString() throws Exception {
        assertEquals(list1,MainActivity.parseGameRecordString("test",string1));
        assertEquals(list2,MainActivity.parseGameRecordString("test",string2));
    }
    @Test
    public void test_parseGameRecordList() throws Exception {
        assertEquals(string1,MainActivity.parseGameRecordList("test",list1));
        assertEquals(string2,MainActivity.parseGameRecordList("test",list2));
    }
}