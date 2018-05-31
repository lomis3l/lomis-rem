package cn.lomis.rem.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.impl.StaticLoggerBinder;

import cn.lomis.rem.bo.Egg;

public class DataUtil {
	
	static Logger logger;
	static Long currentTime = System.currentTimeMillis();

	public static int[] bin2hex(String str) {
        str = str.trim();
        if (str.length() > 0) {
            String[] strs = str.split(" ");
            int[] data = new int[strs.length];
            for (int i = 0; i < strs.length; i++) {
                data[i] = Integer.parseInt(strs[i], 16);
            }
            return data;
        }
        return null;
    }
	
	public static List<Egg> readData(String dataStr, boolean save) {
		List<Egg> eggs = null;
		long time = System.currentTimeMillis();
		if (!save) {
			int firstSpaceIndex = dataStr.indexOf(" ");
			time = Long.valueOf(dataStr.substring(0, firstSpaceIndex));
			dataStr = dataStr.substring(firstSpaceIndex + 1);
		} else {
			if (time == currentTime) {
				currentTime = time + 1;
				time = currentTime;
			}
			logger.info(time + " " + dataStr);
		}
		int[] data = bin2hex(dataStr);
		if (data != null && data.length > 0) {
			eggs = new ArrayList<>();
			for (int i = 0; i < data.length; ) {
				if (data[i++] == 0xAA && (i < data.length - 1) && data[i++] == 0xAA) {
					int len = data[i++] + 1;
					if ((i - 1 + len) > data.length) {
						break;
					}
					int[] src = new int[len];
					System.arraycopy(data, i, src, 0, len);
					int sum = src[len - 1];
					if (sum == ((getSum(src) ^ 0xFFFFFFFF) & 0xFF)) {
						Egg egg = new Egg();
						egg.setTime(time);
						if ((len - 1) == 0x04) {
							int xxHigh = src[2];
							int xxLow  = src[3];
							int rawdata = ((xxHigh << 8) | xxLow);
							if(rawdata > 32768){ rawdata -= 65536;}
							egg.setType(0);
							egg.setRawdata(rawdata);
						} else if ((len - 1) == 0x20) {
							egg.setType(1);
							egg.setSignal      (src[1 ]);	// 信号值
							egg.setDelta       (src[4 ] <<16 | src[5 ] <<8 | src[6 ]);
							egg.setTheta       (src[7 ] <<16 | src[8 ] <<8 | src[9 ]);
							egg.setLowAlpha    (src[9 ] <<16 | src[10] <<8 | src[11]);
							egg.setHighAlpha   (src[12] <<16 | src[13] <<8 | src[14]);
							egg.setLowBeta	   (src[15] <<16 | src[16] <<8 | src[17]);
							egg.setHighBeta    (src[18] <<16 | src[19] <<8 | src[20]);
							egg.setLowGamma    (src[21] <<16 | src[22] <<8 | src[23]);
							egg.setMiddleGamma (src[24] <<16 | src[25] <<8 | src[26]);
							egg.setAttention   (src[28]);
							egg.setMeditation  (src[30]);
						}
						eggs.add(egg);
					}
					i+=len;
				}
			}
		}
		return eggs;
	}
	
	private static int getSum(int[] src) {
		int sum = 0;
		int i = 0;
		while((src.length - 1) > i) {
			sum += src[i];
			i++;
		}
		return sum;
	}
	
	public static void setLog() {
		logger = StaticLoggerBinder.getSingleton().getLoggerFactory().getLogger(DataUtil.class.getName());
	}
	
	public static void main(String[] args) {
		try {
			StaticLoggerBinder.getSingleton().reset();
			String file = "e:\\admin\\Desktop\\其他\\rem\\脑波数据包.txt";
			logger = StaticLoggerBinder.getSingleton().getLoggerFactory().getLogger("ddd");
			List<String> lines = FileUtils.readLines(new File(file));
			for (String line : lines) {
				readData(line, true);
				readData(line, true);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
