package com.zl.zlaiagent.rag;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * 需要本机 PostgreSQL + pgvector，以及 prod 环境变量（SEARCH_API_API_KEY 等）。
 * 默认跳过，避免打断普通 package；服务器配好库后再手动跑。
 */
@SpringBootTest
@ActiveProfiles("prod")
@Disabled("需要 PostgreSQL/pgvector 与 prod 环境变量，打包时跳过")
class PgVectorVectorStoreConfigTest {

    @Test
    void loveAppVectorStore() {
        // 手动验证时取消上方 @Disabled，并配置：
        // SEARCH_API_API_KEY / SPRING_AI_DASHSCOPE_API_KEY / SPRING_DATASOURCE_PASSWORD
    }
}
