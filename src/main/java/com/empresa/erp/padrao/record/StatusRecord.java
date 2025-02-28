package com.empresa.erp.padrao.record;

import com.empresa.erp.padrao.constant.StatusEnum;

import jakarta.validation.constraints.NotNull;

public record StatusRecord(
		
		@NotNull(message = "O status não pode ser nulo.")
		StatusEnum status
			
) {}