package cn.lomis.rem;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.impl.StaticLoggerBinder;

public class TestWriteFile {
	private static Logger logger = LoggerFactory.getLogger(TestWriteFile.class);

	long timestamp = System.currentTimeMillis() / 1000;
	
	@Test
	public void test01() {
		try {
			List<String> lines = new ArrayList<>();
			long time = System.currentTimeMillis() / 1000 - 10000;
			System.out.println(time);
			
			StringBuffer sb = new StringBuffer();
			Random r = new Random(200);
			for (int i = 0; i < 10000; i++) {
				sb.append(time).append(",1:").append(r.nextInt(200)).append("\n");
				System.out.println(time);
				time++;
			}
			FileUtils.writeStringToFile(new File("/media/root/data/lomis/test/rem/data.rem"), sb.toString(), "utf-8");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void test02() {
		try {
			byte[] data = FileUtils.readFileToByteArray(new File("F:\\test\\data.txt"));
			for (int i = 0; i < data.length; ) {
				i = getVal(1, data, i);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 获取脑电值
	 * @param step 1:验证第一步; 2:验证第二部; 3:验证第三步; 4:验证第四步
	 * @param source
	 * @return
	 */
	private int getVal(int step, byte[] source, int index) {
		byte[] dest = new byte[2];
		if (step == 1 || step == 2) {
			System.arraycopy(source, index,  dest, 0, dest.length);
			if ("AA".equals(new String(dest))) {
				return getVal(++step, source, index += dest.length);
			} else {
				index += 2;
			}
		} else if (step == 3) {
			System.arraycopy(source, index, dest, 0, dest.length);
			index += 2;
			int len = Integer.parseInt(new String(dest), 16);
			dest = new byte[(len + 1) * 2];
			System.arraycopy(source, index, dest, 0, dest.length);
			saveData(dest);
			return index +=dest.length;
		}
		return index;
	}

	private void saveData(byte[] data) {
		int sum  = 0;
		int tag  = 0;
		int high = 0;
		int low  = 0;
		byte[] dest = new byte[2];
		for (int j = 0; j < data.length - 2; j+=2) {
			System.arraycopy(data, j,  dest, 0, 2);
			int val = Integer.parseInt(new String(dest), 16);
			if (j == 0) {
				tag = val;
			}
			if (j == 4) {
				high = val;
			}
			if (j == 6) {
				low = val;
			}
			sum += val;
		}
		sum = (sum ^ 0xFFFFFFFF) & 0xFF;
		System.arraycopy(data, data.length - 2,  dest, 0, 2);
		if (Integer.parseInt(new String(dest), 16) == sum) {
			// 验证通过，进一步计算脑电信号值
			long rawdata = (high << 8) | low;
			if (rawdata > 32768) {
				rawdata -= 65535;
			}
			logger.info("{},{}", timestamp++, rawdata);
		}
		
	}
	
	@Test
	public void test03() {
		logger = LoggerFactory.getLogger("ddd");
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		StaticLoggerBinder.getSingleton().reset();
		String absPath = this.getClass().getClassLoader().getResource("").getPath();
		System.out.println(absPath);
		absPath = absPath.substring(1, absPath.lastIndexOf("/"));
		System.out.println(absPath);
		absPath = absPath.substring(0, absPath.lastIndexOf("/"));
		System.out.println(absPath);
		absPath = absPath.substring(0, absPath.lastIndexOf("/"));
		System.out.println(absPath);
		String fileName = StaticLoggerBinder.getSingleton().getFileName();
		System.out.println(absPath + fileName.substring(1));
		/*logger.info("xxxxxx");*/
		
		
		logger = StaticLoggerBinder.getSingleton().getLoggerFactory().getLogger("xxx");
		
		logger.info("xxxxxx");
	}
}
