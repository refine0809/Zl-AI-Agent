package com.zl.zlaiagent;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 本地 profile（local）不配 datasource，由下方 exclude 关掉自动配置；
 * 生产 profile（prod）在 application-prod.yml 中配置 datasource，并取消 exclude。
 * 见 LocalDataSourceExcludeConfig。
 */
@SpringBootApplication
public class ZlAiAgentApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZlAiAgentApplication.class, args);
    }

}

