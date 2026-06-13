package com.fragua.LLMGateway.structure.usage.application.port.output;

import com.fragua.LLMGateway.structure.usage.domain.model.UsageModel;

public interface UsageRecordPort {

    void record(UsageModel usage);
}
