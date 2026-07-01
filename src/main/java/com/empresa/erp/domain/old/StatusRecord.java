package com.empresa.erp.domain.old;

import jakarta.validation.constraints.NotNull;

public record StatusRecord(
		
		@NotNull(message = "O status não pode ser nulo.")
		StatusEnum status
			
) {}