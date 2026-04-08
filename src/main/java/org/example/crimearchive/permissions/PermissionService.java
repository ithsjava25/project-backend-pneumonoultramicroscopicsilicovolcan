package org.example.crimearchive.permissions;

import org.example.crimearchive.repository.SimpleRepository;
import org.springframework.stereotype.Service;

@Service("permittedFilesChecker")
public class PermissionService {

    private final SimpleRepository simpleRepository;
    private final PermissionRepository permissionRepository;

    public PermissionService(SimpleRepository simpleRepository, PermissionRepository permissionRepository) {
        this.simpleRepository = simpleRepository;
        this.permissionRepository = permissionRepository;
    }

//
//    public List<Report> hasPermission(Long accountId){
//    List<String> permittedCaseNumbers = permissionRepository.findAllPermittedReports(accountId);
//    return simpleRepository.findAllByCaseNumberIn(permittedCaseNumbers);
//    }
}
