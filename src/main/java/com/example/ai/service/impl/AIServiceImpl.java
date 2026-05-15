package com.example.ai.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.example.ai.model.request.AIRequest;
import com.example.ai.model.request.AIRequest.RAGContext;
import com.example.ai.model.response.AIResponse;
import com.example.ai.model.response.AIResponse.Choice;
import com.example.ai.model.response.AIResponse.Usage;
import com.example.ai.model.vo.Document;
import com.example.ai.model.vo.Message;
import com.example.ai.service.AIService;

/**
 * AI服务实现类，实现AI对话功能
 * 使用阿里百炼API提供真实的AI能力
 */
@Service
public class AIServiceImpl implements AIService {
    
    private static final Logger log = LoggerFactory.getLogger(AIServiceImpl.class);
    
    private final ChatModel chatModel;
    
    @Autowired
    public AIServiceImpl(ChatModel chatModel) {
        this.chatModel = chatModel;
    }

    @Override
    public AIResponse chat(AIRequest request) {
        log.info("========== AI对话请求开始 ==========");
        log.info("请求ID: {}", request != null ? "N/A" : "null");
        
        // 验证请求参数
        if (request == null || request.getMessages() == null || request.getMessages().isEmpty()) {
            log.error("请求参数验证失败: Messages为空");
            throw new IllegalArgumentException("Messages cannot be empty");
        }
        
        log.info("消息数量: {}", request.getMessages().size());
        log.info("是否启用流式: {}", request.isStream());
        
        // 获取用户最后一条消息
        Message lastMessage = request.getMessages().get(request.getMessages().size() - 1);
        log.info("用户输入: {}", lastMessage.getContent());

        // 构建消息列表
        List<org.springframework.ai.chat.messages.Message> messages = new ArrayList<>();
        
        // 如果有RAG上下文，将相关文档内容添加到对话历史中
        if (request.getRagContext() != null && request.getRagContext().isUseContext()) {
            log.info("✅ 检测到RAG上下文，文档数量: {}", request.getRagContext().getDocuments().size());
            
            // 打印文档标题用于调试
            for (int i = 0; i < request.getRagContext().getDocuments().size(); i++) {
                Document doc = request.getRagContext().getDocuments().get(i);
                log.info("  文档{}: {}", i + 1, doc.getTitle());
            }
            
            String context = buildContextFromDocuments(request.getRagContext().getDocuments());
            String systemPrompt = String.format(
                "你是一个专业的企业AI助手。请**严格基于**以下提供的【相关文档】内容来回答用户问题。\n" +
                "\n" +
                "**重要要求:**\n" +
                "1. **必须只使用文档中的信息**，绝对不要编造或添加文档中没有的内容\n" +
                "2. **如果文档中没有相关信息**，请直接说:'抱歉，知识库中没有找到相关信息'\n" +
                "3. **回答要完整且简洁**：\n" +
                "   - 必须给出完整的回答，不能中途截断\n" +
                "   - 控制在300字以内\n" +
                "   - 如果内容较多，可以精简但要保证完整性\n" +
                "4. **使用Markdown格式**输出，包括标题、列表等\n" +
                "5. **引用具体数据**，如数字、案例、人名等\n" +
                "\n" +
                "**相关文档:**\n" +
                "%s\n" +
                "\n" +
                "**用户问题:** %s\n" +
                "\n" +
                "请基于上述文档直接回答问题（确保回答完整）:",
                context,
                lastMessage.getContent()
            );
            messages.add(new SystemMessage(systemPrompt));
            log.info("已添加系统上下文消息，提示词长度: {} 字符", systemPrompt.length());
        } else {
            log.warn("⚠️ 未检测到RAG上下文，将使用通用提示");
            // 没有RAG时，添加通用系统提示
            messages.add(new SystemMessage(
                "你是一个专业的企业AI助手。回答要简洁明了，控制在200字以内。使用Markdown格式输出。"
            ));
        }
        
        // 添加用户消息
        messages.add(new UserMessage(lastMessage.getContent()));
        
        // 调用真实的AI模型 (参数从 application.properties 读取)
        Prompt prompt = new Prompt(messages);
        
        log.info("开始生成AI响应...");
        long startTime = System.currentTimeMillis();
        
        ChatResponse response = chatModel.call(prompt);
        String responseContent = response.getResult().getOutput().getContent();
        
        long processingTime = System.currentTimeMillis() - startTime;
        log.info("AI响应生成完成，耗时: {}ms", processingTime);
        log.info("响应内容长度: {} 字符", responseContent.length());
        
        // 构建响应
        AIResponse aiResponse = new AIResponse();
        aiResponse.setId("chatcmpl-" + UUID.randomUUID().toString().substring(0, 8));
        aiResponse.setObject("chat.completion");
        aiResponse.setCreated(System.currentTimeMillis() / 1000);
        aiResponse.setModel("qwen-turbo");

        // 构建响应消息
        Message responseMessage = new Message();
        responseMessage.setRole("assistant");
        responseMessage.setContent(responseContent);

        // 设置选项
        List<Choice> choices = new ArrayList<>();
        Choice choice = new Choice();
        choice.setIndex(0);
        choice.setMessage(responseMessage);
        choice.setFinishReason("stop");
        choices.add(choice);
        aiResponse.setChoices(choices);

        // 设置使用情况
        Usage usage = new Usage();
        usage.setPromptTokens(response.getMetadata().getUsage().getPromptTokens().intValue());
        usage.setCompletionTokens(response.getMetadata().getUsage().getGenerationTokens().intValue());
        usage.setTotalTokens(response.getMetadata().getUsage().getTotalTokens().intValue());
        aiResponse.setUsage(usage);
        
        log.info("Token统计 - 输入: {}, 输出: {}, 总计: {}", 
                usage.getPromptTokens(), usage.getCompletionTokens(), usage.getTotalTokens());
        log.info("========== AI对话请求结束 ==========\n");

        return aiResponse;
    }

    @Override
    public SseEmitter streamChat(AIRequest request) {
        log.info("========== 流式AI对话请求开始 ==========");
        log.info("是否启用流式: {}", request != null && request.isStream());
        
        SseEmitter emitter = new SseEmitter();

        // 在新线程中处理流式响应
        CompletableFuture.runAsync(() -> {
            try {
                // 验证请求参数
                if (request == null || request.getMessages() == null || request.getMessages().isEmpty()) {
                    log.error("流式对话请求参数验证失败: Messages为空");
                    emitter.send(SseEmitter.event()
                            .data("Error: Messages cannot be empty")
                            .name("error"));
                    emitter.complete();
                    return;
                }

                // 获取用户最后一条消息
                Message lastMessage = request.getMessages().get(request.getMessages().size() - 1);
                String userContent = lastMessage.getContent();
                log.info("用户输入: {}", userContent);

                // 生成完整响应内容
                log.info("开始生成流式响应...");
                long startTime = System.currentTimeMillis();
                String fullResponse = generateMockResponse(userContent, request.getRagContext());
                char[] chars = fullResponse.toCharArray();
                log.info("响应内容长度: {} 字符", chars.length);

                // 模拟流式响应，分批次发送数据
                int sentCount = 0;
                for (int i = 0; i < chars.length; i++) {
                    // 创建部分响应
                    AIResponse partialResponse = new AIResponse();
                    partialResponse.setId("chatcmpl-stream-" + UUID.randomUUID().toString().substring(0, 8));
                    partialResponse.setObject("chat.completion.chunk");
                    partialResponse.setCreated(System.currentTimeMillis() / 1000);
                    partialResponse.setModel("qwen3.5-flash");

                    // 构建选择项
                    Choice choice = new Choice();
                    choice.setIndex(0);

                    Message msg = new Message();
                    msg.setRole("assistant");
                    msg.setContent(String.valueOf(chars[i]));
                    choice.setMessage(msg);

                    partialResponse.setChoices(Collections.singletonList(choice));
                    partialResponse.setStreamEnd(i == chars.length - 1);

                    // 发送SSE事件
                    emitter.send(SseEmitter.event()
                            .data(partialResponse)
                            .id(String.valueOf(i))
                            .name("chat_completion"));
                    
                    sentCount++;

                    // 模拟网络延迟
                    Thread.sleep(50);
                }
                
                long processingTime = System.currentTimeMillis() - startTime;
                log.info("流式响应发送完成，共发送 {} 个字符块，耗时: {}ms", sentCount, processingTime);
                log.info("========== 流式AI对话请求结束 ==========\n");

                emitter.complete();
            } catch (Exception e) {
                log.error("流式对话处理异常: {}", e.getMessage(), e);
                emitter.completeWithError(e);
            }
        });

        return emitter;
    }

    /**
     * 从文档列表构建上下文内容
     */
    private String buildContextFromDocuments(List<Document> documents) {
        if (documents == null || documents.isEmpty()) {
            return "";
        }

        StringBuilder context = new StringBuilder();
        for (Document doc : documents) {
            context.append("文档标题：").append(doc.getTitle()).append("\n");
            context.append("文档内容：").append(doc.getContent()).append("\n\n");
        }
        return context.toString();
    }

    /**
     * 生成模拟响应
     *
     * @param userContent 用户输入内容
     * @param ragContext  RAG上下文
     * @return 模拟的AI响应内容
     */
    private String generateMockResponse(String userContent, RAGContext ragContext) {
        log.debug("生成模拟响应，输入长度: {}", userContent.length());
        
        // 简单的关键词匹配，生成相应的回复
        String lowerContent = userContent.toLowerCase();

        // 打招呼相关
        if (lowerContent.contains("你好") || lowerContent.contains("hello") || lowerContent.contains("hi") ||
            lowerContent.contains("哈喽") || lowerContent.contains("嗨") || lowerContent.contains("您好") ||
            lowerContent.contains("早上好") || lowerContent.contains("下午好") || lowerContent.contains("晚上好")) {
            return "你好！我是一个AI助手，可以帮助你解答问题、提供信息或者进行交流。请问有什么我可以帮助你的吗？";
        } else if (lowerContent.contains("你是谁") || lowerContent.contains("what are you")) {
            return "我是一个基于人工智能技术的助手，可以帮助你解答问题、提供信息和进行各种对话。我由开发者使用Spring Boot和AI技术构建，旨在为用户提供智能化的服务。";
        } else if (lowerContent.contains("谢谢") || lowerContent.contains("感谢") || lowerContent.contains("thank")) {
            return "不客气！很高兴能够帮助到你。如果还有其他问题，随时可以问我。";
        } else if (lowerContent.contains("再见") || lowerContent.contains("bye") || lowerContent.contains("goodbye")) {
            return "再见！希望能够再次为你提供帮助。祝你有美好的一天！";
        } else if (lowerContent.contains("帮助") || lowerContent.contains("help")) {
            return "我可以帮助你解答各种问题，提供信息，或者进行日常对话。你可以问我关于科技、历史、文化、生活等各个方面的问题，我会尽力给出准确的回答。";
        }
        // 公司相关问题
        else if (lowerContent.contains("公司") && (lowerContent.contains("成立") || lowerContent.contains("创建"))) {
            return "根据文档内容，我们公司成立于2018年，是一家专注于人工智能技术研发的高科技企业，总部位于北京。";
        } else if (lowerContent.contains("公司") && lowerContent.contains("业务") || lowerContent.contains("产品")) {
            return "根据文档内容，公司的主营业务包括人工智能技术研发，涵盖自然语言处理、计算机视觉和机器学习等领域。主要产品包括智能客服系统、图像识别平台和数据分析工具。";
        } else if (lowerContent.contains("团队") || lowerContent.contains("人员")) {
            return "根据文档内容，公司拥有一支由顶尖AI专家组成的技术团队，其中包括5名博士和10名硕士。团队成员来自国内外知名高校和研究机构，在AI领域拥有丰富的研发经验。";
        } else if (lowerContent.contains("发展") || lowerContent.contains("历程")) {
            return "根据文档内容，公司2018年在北京成立，2019年发布第一款智能客服产品，2020年获得A轮融资1000万元，2021年产品覆盖多个行业，2022年获得B轮融资5000万元，2023年员工规模扩大到100人，年营收突破1亿元。";
        } else if (lowerContent.contains("客户") || lowerContent.contains("案例")) {
            return "根据文档内容，我们的客户包括多家Fortune 500强企业。在金融行业，帮助某大型银行提升了客服效率30%；在电商领域，为某知名平台开发了智能推荐系统，提升销售额20%；在医疗行业，协助某三甲医院开发了医学影像辅助诊断系统，提高诊断准确率15%。";
        } else if (lowerContent.contains("文化") || lowerContent.contains("价值观")) {
            return "根据文档内容，公司秉承'创新、协作、卓越'的企业文化。创新是核心驱动力，协作是工作方式，卓越是目标。";
        } else if (lowerContent.contains("技术") || lowerContent.contains("优势")) {
            return "根据文档内容，公司拥有多项自主研发的AI核心技术，包括大规模预训练模型、多模态融合算法和实时推理引擎。技术优势在于能够将复杂的AI算法转化为易用的产品和服务。";
        } else if (lowerContent.contains("未来") || lowerContent.contains("规划")) {
            return "根据文档内容，未来三年，公司计划进一步扩大技术团队规模，加强在多模态AI和行业大模型领域的研发投入。将重点发展垂直行业解决方案，特别是在金融、医疗和智能制造领域。";
        }
        // 如果有RAG上下文，但没有匹配到具体问题
        else if (ragContext != null && ragContext.isUseContext() && ragContext.getDocuments() != null && !ragContext.getDocuments().isEmpty()) {
            return "根据检索到的相关文档，我可以为你提供以下信息：\n\n" +
                    buildContextFromDocuments(ragContext.getDocuments()) +
                    "\n基于这些信息，你可以进一步明确你的问题，我会为你提供更精准的回答。";
        } else {
            // 默认回复
            return "感谢你的提问！这是一个模拟的AI响应。在实际应用中，这里会调用真实的AI API来生成更准确、更有意义的回答。你可以尝试问我一些关于公司的问题，比如公司成立时间、主营业务、技术团队等。";
        }
    }
}