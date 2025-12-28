CREATE TABLE chatbot_users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    student_id VARCHAR(50) NOT NULL COMMENT '学号',
    name VARCHAR(100) NOT NULL COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    user_draw TEXT COMMENT '用户绘图数据',
    UNIQUE KEY uk_student_id (student_id),
    KEY idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

CREATE TABLE chatbot_chat_history (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '聊天历史ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    name VARCHAR(200) NOT NULL COMMENT '聊天名称',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    context_count INT DEFAULT 0 COMMENT '上下文数量',
    summary_count INT DEFAULT 0 COMMENT '摘要数量',
    knowledge TEXT COMMENT '知识点',
    KEY idx_user_id (user_id),
    KEY idx_create_time (create_time),
    CONSTRAINT fk_chat_history_user
        FOREIGN KEY (user_id) REFERENCES chatbot_users(id)
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='聊天历史表';

CREATE TABLE chatbot_chat_details (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '聊天详情ID',
    chat_history_id BIGINT NOT NULL COMMENT '聊天历史ID',
    user_chat TEXT COMMENT '用户消息',
    ai_chat TEXT COMMENT 'AI回复',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    KEY idx_chat_history_id (chat_history_id),
    KEY idx_create_time (create_time),
    CONSTRAINT fk_chat_details_history
        FOREIGN KEY (chat_history_id) REFERENCES chatbot_chat_history(id)
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='聊天详情表';

CREATE TABLE chatbot_chat_summary (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '摘要ID',
    chat_history_id BIGINT NOT NULL COMMENT '聊天历史ID',
    summary TEXT COMMENT '摘要内容',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    KEY idx_chat_history_id (chat_history_id),
    KEY idx_create_time (create_time),
    CONSTRAINT fk_chat_summary_history
        FOREIGN KEY (chat_history_id) REFERENCES chatbot_chat_history(id)
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='聊天摘要表';

CREATE TABLE chatbot_question_bank (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '题目ID',
    name VARCHAR(200) COMMENT '题目名称',
    question_common TEXT COMMENT '通用问题',
    question_knowledge TEXT COMMENT '知识点',
    description TEXT COMMENT '题目描述',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    KEY idx_name (name),
    KEY idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='题库表';

CREATE TABLE chatbot_common_question (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '常见问题ID',
    question_id BIGINT NOT NULL COMMENT '题目ID',
    description TEXT COMMENT '问题描述',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    KEY idx_question_id (question_id),
    KEY idx_create_time (create_time),
    CONSTRAINT fk_common_question_bank
        FOREIGN KEY (question_id) REFERENCES chatbot_question_bank(id)
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='常见问题表';

CREATE TABLE chatbot_collection (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '收藏ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    question_id BIGINT NOT NULL COMMENT '题目ID',
    UNIQUE KEY uk_user_question (user_id, question_id),
    KEY idx_user_id (user_id),
    KEY idx_question_id (question_id),
    CONSTRAINT fk_collection_user
        FOREIGN KEY (user_id) REFERENCES chatbot_users(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_collection_question
        FOREIGN KEY (question_id) REFERENCES chatbot_question_bank(id)
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收藏表';

CREATE TABLE chatbot_problem (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '题目ID',
    title TEXT COMMENT '题目标题',
    description TEXT COMMENT '题目描述',
    difficulty INT COMMENT '难度等级',
    source TEXT COMMENT '来源',
    author_name VARCHAR(100) COMMENT '作者名称',
    time_limit INT COMMENT '时间限制(ms)',
    memory_limit INT COMMENT '内存限制(MB)',
    input_description TEXT COMMENT '输入描述',
    output_description TEXT COMMENT '输出描述',
    solution TEXT COMMENT '解决方案',
    sample_input TEXT COMMENT '样例输入',
    sample_output TEXT COMMENT '样例输出',
    test_input TEXT COMMENT '测试输入',
    test_output TEXT COMMENT '测试输出',
    include_sample TINYINT(1) DEFAULT 0 COMMENT '是否包含样例',
    is_full TINYINT(1) DEFAULT 0 COMMENT '是否完整',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    KEY idx_difficulty (difficulty),
    KEY idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='编程题目表';

