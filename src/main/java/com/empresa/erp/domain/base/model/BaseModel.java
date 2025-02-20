package com.empresa.erp.domain.base.model;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BaseModel {

	@NotNull
	private Boolean ativo;
	@NotNull
	private Boolean removido;
	
	public BaseModel() {
		this.ativo = true;
		this.removido = false;
	}
	
	public void ativo(Boolean ativo) {		
		this.ativo = ativo;		
	}
	
	public void removido(Boolean removido) {		
		this.removido = removido;
	}
	
}