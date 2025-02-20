package com.empresa.erp.domain.usuario.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.empresa.erp.domain.usuario.repository.UsuarioRepository;

@Service
public class UsuarioService implements UserDetailsService{

	@Autowired
	private UsuarioRepository repository;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		return repository.findByEmail(username);
	}
	
}
