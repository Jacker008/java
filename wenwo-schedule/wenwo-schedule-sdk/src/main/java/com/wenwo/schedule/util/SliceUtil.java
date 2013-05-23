package com.wenwo.schedule.util;

import java.util.Arrays;
import java.util.List;

/**
 * @author yuxuan.wang
 * 
 */
public final class SliceUtil {

	/**
	 * 将一个集合分片
	 * 
	 * @param full
	 *            原始集合
	 * @param page
	 *            分片大小
	 * @return
	 */
	public static List<?>[] slice(List<?> full, int page) {
		if (full == null || full.size() == 0 || page <= 0)
			throw new IllegalArgumentException();
		final int size = full.size();
		final int sliceCount = (int) Math.ceil(((float) size) / page);
		final List<?>[] slices = new List<?>[sliceCount];
		for (int i = 0; i < sliceCount; i++) {
			final int fromIndex = page * i;
			int toIndex = page * (i + 1);
			if (toIndex > size)
				toIndex = size;
			slices[i] = full.subList(fromIndex, toIndex);
		}
		return slices;
	}

	public static void main(String[] args) {
		List<String> all = Arrays.asList("1", "2", "3", "4", "5", "6", "7", "8", "9");
		System.out.println(Arrays.toString(slice(all, 2)));
		System.out.println(Arrays.toString(slice(all, 3)));
		System.out.println(Arrays.toString(slice(all, 4)));
		System.out.println(Arrays.toString(slice(all, 9)));
		System.out.println(Arrays.toString(slice(all, 10)));
	}

}
