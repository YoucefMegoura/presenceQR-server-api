package com.youcefmegoura.presenceqr.service;

import com.youcefmegoura.presenceqr.model.AdRole;
import com.youcefmegoura.presenceqr.repository.AdRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AdRoleService {

    private final AdRoleRepository adRoleRepository;

    @Autowired
    public AdRoleService(AdRoleRepository adRoleRepository) {
        this.adRoleRepository = adRoleRepository;
    }

    public List<AdRole> findAll() {
        return adRoleRepository.findAll();
    }

    public Optional<AdRole> findById(Long id) {
        return adRoleRepository.findById(id);
    }

    public AdRole save(AdRole adRole) {
        return adRoleRepository.save(adRole);
    }

    public void deleteById(Long id) {
        adRoleRepository.deleteById(id);
    }
}