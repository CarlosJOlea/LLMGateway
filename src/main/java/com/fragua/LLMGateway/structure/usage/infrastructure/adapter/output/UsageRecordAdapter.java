package com.fragua.LLMGateway.structure.usage.infrastructure.adapter.output;

import com.fragua.LLMGateway.structure.usage.application.port.output.UsageRecordPort;
import com.fragua.LLMGateway.structure.usage.domain.model.UsageModel;
import com.fragua.LLMGateway.structure.usage.infrastructure.adapter.output.entity.UsageRecord;
import com.fragua.LLMGateway.structure.usage.infrastructure.adapter.output.repo.UsageRecordJpaRepository;
import com.fragua.LLMGateway.structure.user.infraestructure.output.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class UsageRecordAdapter implements UsageRecordPort {

    private final UsageRecordJpaRepository usageRecordJpaRepository;

    @Override
    public void record(UsageModel usage) {

        try {
            User user = new User();
            user.setId(usage.userId());

            int prompt = usage.promptTokens() != null ? usage.promptTokens() : 0;
            int completion = usage.completionTokens() != null ? usage.completionTokens() : 0;

            usageRecordJpaRepository.save(UsageRecord.builder()
                    .modelName(usage.modelName())
                    .promptTokens(prompt)
                    .completionTokens(completion)
                    .totalTokens(prompt + completion)
                    .source(usage.source())
                    .createdAt(LocalDateTime.now())
                    .user(user)
                    .build());
        } catch (Exception e) {
            // Registrar uso nunca debe tumbar la peticion principal
            log.error("No se pudo registrar el uso de tokens: {}", e.getMessage());
        }
    }
}
