package cn.cstn.algorithm.commons;


import lombok.Getter;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.IntStream;

/**
 * description :        通用泛型并查集（Union-Find），支持任意类型的元素
 *
 * @author :            dterazhao
 * date :               2026/4/15
 */
public class UnionFind<T> {
  private final Map<T, T> parent;
  private final Map<T, Integer> sz;
  @Getter
  protected int numOfComponent;
  @Getter
  protected final Set<T> roots;

  public UnionFind() {
    parent = new ConcurrentHashMap<>();
    sz = new ConcurrentHashMap<>();
    numOfComponent = 0;
    roots = ConcurrentHashMap.newKeySet();
  }

  /**
   * 添加一个新元素到并查集中（如果尚未存在）
   */
  public synchronized void add(T item) {
    if (!parent.containsKey(item)) {
      parent.put(item, item);
      sz.put(item, 1);
      roots.add(item);
      numOfComponent++;
    }
  }

  /**
   * 添加集合中的所有元素到并查集中（如果尚未存在）
   */
  public void addAll(Set<T> set) {
    for (T id : set) {
      add(id);
    }
  }

  /**
   * 判断两个元素是否属于同一连通分量
   */
  public boolean isConnected(T p, T q) {
    return find(p).equals(find(q));
  }

  /**
   * 查找元素的根节点（带路径压缩）
   */
  public T find(T p) {
    if (!parent.containsKey(p)) {
      add(p);
    }
    T r = p;
    while (!parent.get(r).equals(r)) {
      r = parent.get(r);
    }
    // 路径压缩
    T cur = p;
    while (!cur.equals(r)) {
      T next = parent.get(cur);
      parent.put(cur, r);
      cur = next;
    }
    return r;
  }

  /**
   * 合并两个元素所在的连通分量（按大小合并）
   */
  public void union(T p, T q) {
    T pr = find(p);
    T qr = find(q);
    if (pr.equals(qr)) {
      return;
    }
    if (sz.get(pr) < sz.get(qr)) {
      parent.put(pr, qr);
      sz.put(qr, sz.get(qr) + sz.get(pr));
      roots.remove(pr);
    } else {
      parent.put(qr, pr);
      sz.put(pr, sz.get(pr) + sz.get(qr));
      roots.remove(qr);
    }
    numOfComponent--;
  }

  /**
   * 获取所有属于同一连通分量的元素分组
   */
  public Map<T, Set<T>> getComponents() {
    Map<T, Set<T>> components = new LinkedHashMap<>();
    for (T item : parent.keySet()) {
      T root = find(item);
      components.computeIfAbsent(root, k -> new HashSet<>()).add(item);
    }
    return components;
  }

  /**
   * 兼容原有基于int的并查集用法
   */
  public static class IntUF extends UnionFind<Integer> {
    public IntUF(int n) {
      super();
      IntStream.range(0, n).forEach(this::add);
    }
  }
}

