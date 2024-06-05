package home.office.spring.domain.fiscal.fornecedor.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import home.office.spring.domain.fiscal.endereco.model.EnderecoModel;
import home.office.spring.domain.fiscal.endereco.repository.EnderecoRepository;
import home.office.spring.domain.fiscal.fornecedor.model.FornecedorModel;
import home.office.spring.domain.fiscal.fornecedor.record.AtualizaFornecedorRecord;
import home.office.spring.domain.fiscal.fornecedor.record.DetalheFornecedorRecord;
import home.office.spring.domain.fiscal.fornecedor.record.FornecedorRecord;
import home.office.spring.domain.fiscal.fornecedor.record.ListaFornecedorRecord;
import home.office.spring.domain.fiscal.fornecedor.repository.FornecedorRepository;
import home.office.spring.domain.fiscal.regimeTributacaoFederal.model.RegimeTributacaoFederalModel;
import home.office.spring.domain.fiscal.regimeTributacaoFederal.repository.RegimeTributacaoFederalRepository;
import home.office.spring.domain.fiscal.setorAtividade.model.SetorAtividadeModel;
import home.office.spring.domain.fiscal.setorAtividade.repository.SetorAtividadeRepository;
import home.office.spring.infra.exception.ValidacaoException;
import jakarta.transaction.Transactional;

@Service
public class FornecedorService {
	
	@Autowired
	private FornecedorRepository repository;
	
	@Autowired
	private RegimeTributacaoFederalRepository regimeTributacaoFederalRepository;
	
	@Autowired
	private SetorAtividadeRepository setorAtividadeRepository;
	
	@Autowired
	private EnderecoRepository enderecoRepository;
	
	@Transactional
	public FornecedorModel cadastrar(FornecedorRecord dados) {
		
		try {
			
			var endereco = new EnderecoModel(dados.endereco());
			
			enderecoRepository.save(endereco);
			
			var regimeTributacaoFederal = regimeTributacaoFederalRepository.getReferenceById(dados.regimeTributacaoFederal().id());
			
			var setorAtividade = setorAtividadeRepository.getReferenceById(dados.setorAtividade().id());
			
			var fornecedor = new FornecedorModel(dados, regimeTributacaoFederal, setorAtividade, endereco);
			
			repository.save(fornecedor);
			
			return fornecedor;
			
		} catch (ValidacaoException e) {
			
			throw new ValidacaoException("Não foi possível realizar o cadastro do fornecedor.");
			
		}
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public Page<ListaFornecedorRecord> listar(Pageable paginacao, String filtro) {
		try {
			if(filtro != null) {
				return repository.findByRazaoSocialContaining(paginacao, filtro).map(ListaFornecedorRecord::new);
			} else {			
				return repository.findAllByAtivoTrue(paginacao).map(ListaFornecedorRecord::new);
			}
		} catch (ValidacaoException e) {
			throw new ValidacaoException("Não foi possível realizar a listagem dos fornecedores.");
		}
	}
	
	@Transactional
	public DetalheFornecedorRecord atualizar(AtualizaFornecedorRecord dados) {
		try {
			RegimeTributacaoFederalModel regimeTributacaoFederal = regimeTributacaoFederalRepository.getReferenceById(dados.regimeTributacaoFederal().id());
			SetorAtividadeModel setorAtividade = setorAtividadeRepository.getReferenceById(dados.setorAtividade().id());
			FornecedorModel fornecedor = repository.getReferenceById(dados.id());
			fornecedor.atualizar(dados, regimeTributacaoFederal, setorAtividade);
			return new DetalheFornecedorRecord(fornecedor);
		} catch (ValidacaoException e) {
			throw new ValidacaoException("Não foi possível realizar a atualização do fornecedor.");
		}
	}
	
	@Transactional
	public void excluir(Long id, Boolean ativo) {
		try {
			repository.getReferenceById(id).ativo(ativo);
		} catch (ValidacaoException e) {
			throw new ValidacaoException("Não foi possível realizar a exclusão do fornecedor.");
		}
	}
	
	public DetalheFornecedorRecord detalhar(Long id) {
		try {
			FornecedorModel fornecedor = repository.getReferenceById(id);
			return new DetalheFornecedorRecord(fornecedor);
		} catch (ValidacaoException e) {
			throw new ValidacaoException("Não foi possível realizar o detalhamento do fornecedor.");
		}
	}
	
}
