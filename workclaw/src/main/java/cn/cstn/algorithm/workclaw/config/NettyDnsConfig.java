package cn.cstn.algorithm.workclaw.config;

import io.netty.channel.ChannelOption;
import io.netty.resolver.DefaultAddressResolverGroup;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

import java.time.Duration;

/**
 * Netty DNS 配置
 * <p>
 * 解决 macOS 上 Netty UDP DNS 解析器的兼容性问题（SocketException: Unknown error: 65435）
 * 通过配置使用正确的 DNS 解析器实现来避免 UDP 网络栈问题
 * </p>
 *
 * @author zhaohuiqiang
 */
@Slf4j
@Configuration
public class NettyDnsConfig {

  @PostConstruct
  public void init() {
    try {
      // 设置 macOS 使用原生 DNS 解析器（已通过 pom.xml 添加依赖）
      // 这会在 Netty 启动时自动加载正确的 DNS 解析器
      System.setProperty("io.netty.resolver.dns.preferNative", "true");
      
      // 禁用 UDP DNS 查询的 retry，避免 macOS 上的 UDP 错误
      System.setProperty("io.netty.resolver.dns.maxQueriesPerResolve", "1");
      
      // 使用 TCP 作为 DNS 解析的备选方案
      System.setProperty("io.netty.resolver.dns.enableTcp", "true");
      
      log.info("Netty DNS 配置完成：启用原生 macOS DNS 解析器");
    } catch (Exception e) {
      log.warn("Netty DNS 配置失败，将使用默认配置: {}", e.getMessage());
    }
  }

  /**
   * 创建配置好的 HttpClient，用于 MCP 传输层
   * 使用阻塞式 DNS 解析器避免 macOS 上的 UDP 问题
   */
  public static HttpClient createHttpClient() {
    ConnectionProvider connectionProvider = ConnectionProvider.builder("mcp-client")
      .maxConnections(100)
      .maxIdleTime(Duration.ofSeconds(60))
      .maxLifeTime(Duration.ofSeconds(300))
      .pendingAcquireTimeout(Duration.ofSeconds(60))
      .evictInBackground(Duration.ofSeconds(120))
      .build();

    return HttpClient.create(connectionProvider)
      .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)
      .option(ChannelOption.SO_KEEPALIVE, true)
      .responseTimeout(Duration.ofSeconds(60))
      // 使用默认的阻塞式 DNS 解析器，避免 macOS UDP 问题
      .resolver(DefaultAddressResolverGroup.INSTANCE);
  }

}
