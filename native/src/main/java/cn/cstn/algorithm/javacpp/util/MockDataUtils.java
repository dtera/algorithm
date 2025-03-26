package cn.cstn.algorithm.javacpp.util;

import cn.hutool.core.lang.Pair;

import java.util.Random;
import java.util.function.Function;

public class MockDataUtils {

  public static int[][] mockHistIndexes(final int n, final int num_features, final int num_bins) {
    Random rng = new Random();
    return mockHistIndexes(n, num_features, num_bins, (Pair<Integer, Integer> p) -> rng.nextInt(num_bins));
  }

  public static int[][] mockHistIndexes(final int n, final int num_features, final int num_bins,
                                        Function<Pair<Integer, Integer>, Integer> binAllocator) {
    int total_bins = num_features * num_bins;
    int[][] indexes = new int[total_bins][];
    int[] indexSize = new int[total_bins];

    for (int j = 0; j < num_features; ++j) {
      int[] flag = new int[n];
      for (int i = 0; i < n; ++i) {
        flag[i] = binAllocator.apply(Pair.of(i, j));
        indexSize[j * num_bins + flag[i]]++;
      }

      for (int i = 0; i < num_bins; ++i) {
        int k = j * num_bins + i;
        indexes[k] = new int[indexSize[k]];
        indexSize[k] = 0;
      }

      for (int i = 0; i < n; ++i) {
        int k = j * num_bins + flag[i];
        indexes[k][indexSize[k]] = i;
        indexSize[k]++;
      }
    }

    return indexes;
  }

  public static double[] mockGrads(final int n) {
    Random rng = new Random();
    double[] grads = new double[n];
    for (int i = 0; i < n; ++i) {
      grads[i] = rng.nextGaussian();
    }
    return grads;
  }

}
