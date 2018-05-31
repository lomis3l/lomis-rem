package cn.lomis.rem;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

public class TestHex {
	
	@Test
	public void test01() {
//		int sum = ((0x80 + 0x02 + 0xFF + 0xC2) ^ 0xFFFFFFFF) & 0xFF;
		int sum = ((Integer.parseInt("80", 16) + Integer.parseInt("02", 16) + Integer.parseInt("FF", 16) + Integer.parseInt("C2", 16)) ^ 0xFFFFFFFF) & 0xFF;
		System.out.println(sum);
		System.out.println(Integer.toHexString(sum));
		System.out.println(Integer.parseInt("BC", 16));
	}
	
	@Test
	public void test02() {
		String dataStr = "1 AA AA BB FF 01";
		int firstSpaceIndex = dataStr.indexOf(" ");
		System.out.println(firstSpaceIndex);
		
		System.out.println(dataStr.substring(0, firstSpaceIndex));
		System.out.println(dataStr.substring(firstSpaceIndex));
	}
	
	@Test
	public void test03 () {
		String filename = "e:/rem/logs/dddd_ddd.log";
		System.out.println(filename.replaceAll("logs", "data").replaceAll("\\.log", "_" + System.currentTimeMillis() + "\\.data"));
	}
	
	@Test
	public void test04() {
		Map<Integer, String> map = new HashMap<>();
		map.put(0, "零");
		map.put(1, "一");
		map.put(2, "二");
		map.put(3, "三");
		map.put(4, "四");
		map.put(5, "五");
		map.put(6, "六");
		map.put(7, "七");
		map.put(8, "八");
		map.put(9, "九");
		map.put(10, "十");
		map.put(11, "十一");
		map.put(12, "十二");
		map.put(13, "十三");
		map.put(14, "十四");
		map.put(15, "十五");
		map.put(16, "十六");
		map.put(17, "十七");
		map.put(18, "十八");
		map.put(19, "十九");
		map.put(20, "二十");
		List<String> list = new ArrayList<>();
		for (int a = 0; a <=10; a++) {
			for (int b = 0; b <= 10; b++) {
				StringBuffer sBuffer = new StringBuffer();
				sBuffer.append(map.get(a));
				sBuffer.append("加");
				sBuffer.append(map.get(b));
				if (a+b <= 10) {
					sBuffer.append("得");
					sBuffer.append(map.get(a+b));
				} else {
					String c = String.valueOf(a + b);
					char[] cs = c.toCharArray();
					for (int i = 0; i < cs.length; i++) {
						Integer key = Integer.valueOf(String.valueOf(cs[i]));
						if (i > 0 && key == 0) {
							key = 10;
						}
						sBuffer.append(map.get(key));
					}
				}
				
				System.out.println(sBuffer.toString());
				list.add(sBuffer.toString());
			}
		}
		try {
			FileUtils.writeLines(new File("D:/加法口诀.txt"), list);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void test05() {
		Map<Integer, String> map = new HashMap<>();
		map.put(0, "零");
		map.put(1, "一");
		map.put(2, "二");
		map.put(3, "三");
		map.put(4, "四");
		map.put(5, "五");
		map.put(6, "六");
		map.put(7, "七");
		map.put(8, "八");
		map.put(9, "九");
		map.put(10, "十");
		map.put(11, "十一");
		map.put(12, "十二");
		map.put(13, "十三");
		map.put(14, "十四");
		map.put(15, "十五");
		map.put(16, "十六");
		map.put(17, "十七");
		map.put(18, "十八");
		map.put(19, "十九");
		map.put(20, "二十");
		List<String> list = new ArrayList<>();
		for (int a = 20; a >= 0; a--) {
			for (int b = 10; b >= 0; b--) {
				StringBuffer sBuffer = new StringBuffer();
				sBuffer.append(map.get(a));
				sBuffer.append("减");
				sBuffer.append(map.get(b));
				if (a-b <= 10) {
					sBuffer.append("得");
					sBuffer.append(map.get(a-b));
				} else {
					String c = String.valueOf(a - b);
					char[] cs = c.toCharArray();
					for (int i = 0; i < cs.length; i++) {
						Integer key = Integer.valueOf(String.valueOf(cs[i]));
						if (i > 0 && key == 0) {
							key = 10;
						}
						sBuffer.append(map.get(key));
					}
				}
				
				System.out.println(sBuffer.toString());
				list.add(sBuffer.toString());
			}
		}
		try {
			FileUtils.writeLines(new File("D:/减法口诀.txt"), list);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
