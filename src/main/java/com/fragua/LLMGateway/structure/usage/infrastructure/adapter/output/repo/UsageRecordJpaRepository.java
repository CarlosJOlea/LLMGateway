package com.fragua.LLMGateway.structure.usage.infrastructure.adapter.output.repo;

import com.fragua.LLMGateway.structure.usage.infrastructure.adapter.output.entity.UsageRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UsageRecordJpaRepository extends JpaRepository<UsageRecord, UUID> {
}
