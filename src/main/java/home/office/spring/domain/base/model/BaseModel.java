package home.office.spring.domain.base.model;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BaseModel {

	@NotNull
	private Boolean ativo;
	@NotNull
	private Boolean remove;
	
	public BaseModel() {
		this.ativo = true;
		this.remove = false;
	}
	
	public void ativo(Boolean ativo) {		
		this.ativo = ativo;		
	}
	
	public void remove(Boolean remove) {		
		this.remove = remove;
	}
	
}