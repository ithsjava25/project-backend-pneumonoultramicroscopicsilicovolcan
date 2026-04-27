package org.example.crimearchive.controllers;

import jakarta.validation.Valid;
import org.example.crimearchive.DTO.Polis.DTOUpdateProfile;
import org.example.crimearchive.cases.CaseLifecycleService;
import org.example.crimearchive.cases.CaseService;
import org.example.crimearchive.cases.CaseStatus;
import org.example.crimearchive.cases.Cases;
import org.example.crimearchive.evidence.EvidenceFileService;
import org.example.crimearchive.exceptions.PasswordValidationException;
import org.example.crimearchive.permissions.PermissionService;
import org.example.crimearchive.polis.Account;
import org.example.crimearchive.polis.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Controller
public class ProfileController {

    private final CaseService caseService;
    private final UserService userService;
    private final CaseLifecycleService lifecycleService;
 private final EvidenceFileService evidenceFileService;                                                                                                                                                                                                                                     
      private final PermissionService permissionService;                                                                                                                                                                                                                                         
                                                                                                                                                                                                                                                                                                 
      public ProfileController(CaseService caseService, UserService userService,                                                                                                                                                                                                                 
                               CaseLifecycleService lifecycleService,
                               EvidenceFileService evidenceFileService,
                               PermissionService permissionService) {                                                                                                                                                                                                                            
          this.caseService = caseService;
          this.userService = userService;                                                                                                                                                                                                                                                        
          this.lifecycleService = lifecycleService;
          this.evidenceFileService = evidenceFileService;
          this.permissionService = permissionService;                                                                                                                                                                                                                                            
      }


    @GetMapping("/profile")
    public String profilePage(@AuthenticationPrincipal Account user, Model model) {
        prepareModel(model, user);
        model.addAttribute("updateProfile", userService.prefillProfileFields(user));
        model.addAttribute("searchCases", caseService.getOpenCases(CaseStatus.CLOSED));
        return "profile";
    }

    @PostMapping("/profile/update")
    public String updateProfile(@ModelAttribute("updateProfile") @Valid DTOUpdateProfile profileUpdate,
                                BindingResult bindingResult,
                                @AuthenticationPrincipal Account user,
                                Model model){
        prepareModel(model, user);
        if(bindingResult.hasErrors()){
            return "profile";
        }
        try {
            userService.updateProfile(new DTOUpdateProfile(profileUpdate.fullname(),
                    profileUpdate.password(), user.getId()));
        }catch(PasswordValidationException e){
            bindingResult.rejectValue("password", "updateProfile", e.getMessage());
            return "profile";
        }
        return "redirect:/profile";
    }

    @GetMapping("/caseoverview")
    public String caseoverview(@AuthenticationPrincipal Account user,
                               Model model, @RequestParam String casenumber) {
        prepareModel(model, user);
        model.addAttribute("canAdd", permissionService.canAccessCase(casenumber, user));
        model.addAttribute("assignedPolice", caseService.getAllPoliceForCase(casenumber));
        model.addAttribute("rawcasenumber", casenumber);
        model.addAttribute("reportList", caseService.getReportSet(casenumber));
        model.addAttribute("caseStatus", lifecycleService.getStatus(casenumber));
        model.addAttribute("allStatuses", CaseStatus.values());
        model.addAttribute("events", lifecycleService.getEvents(casenumber));
        model.addAttribute("comments", lifecycleService.getComments(casenumber));
        model.addAttribute("evidenceFiles", evidenceFileService.getLatestVersionsByCaseNumber(casenumber));
        return "caseoverview";
    }

    @PostMapping("/caseoverview/status")
    @PreAuthorize("@caseSecurity.canAccessCase(#casenumber, principal)")
    public String changeStatus(@RequestParam String casenumber,
                               @RequestParam CaseStatus status,
                               @AuthenticationPrincipal Account user) {
        lifecycleService.changeStatus(casenumber, status, user.getUsername());
        return "redirect:/caseoverview?casenumber=" + URLEncoder.encode(casenumber, StandardCharsets.UTF_8);
    }

    @PostMapping("/caseoverview/comment")
    public String addComment(@RequestParam String casenumber,
                             @RequestParam String content,
                             @AuthenticationPrincipal Account user) {
        if (content != null && !content.isBlank()) {
            if (content.length() > 2000) {
                return "redirect:/caseoverview?casenumber=" + URLEncoder.encode(casenumber, StandardCharsets.UTF_8) + "&commentError=1";
            }
            lifecycleService.addComment(casenumber, content, user.getUsername());
        }
        return "redirect:/caseoverview?casenumber=" + URLEncoder.encode(casenumber, StandardCharsets.UTF_8);
    }

    @PostMapping("/caseoverview/upload")
    @PreAuthorize("@caseSecurity.canAccessCase(#casenumber, principal)")
    public String uploadEvidence(@RequestParam String casenumber,
                                 @RequestParam(required = false) List<MultipartFile> files,
                                 @RequestParam(required = false) List<MultipartFile> images,
                                 @AuthenticationPrincipal Account user) {
        String redirect = "redirect:/caseoverview?casenumber=" + URLEncoder.encode(casenumber, StandardCharsets.UTF_8);
        try {
            if (files != null) {
                for (MultipartFile file : files) {
                    if (!file.isEmpty()) {
                        evidenceFileService.upload(casenumber, casenumber, "Uppladdad från ärendeöversikt", file, user.getUsername());
                    }
                }
            }
            if (images != null) {
                for (MultipartFile image : images) {
                    if (!image.isEmpty()) {
                        evidenceFileService.upload(casenumber, casenumber, "Uppladdad från ärendeöversikt", image, user.getUsername());
                    }
                }
            }
        } catch (IOException e) {
            return redirect + "&uploadError=1";
        }
        return redirect;
    }

    @PostMapping("/caseoverview/replace")
    @PreAuthorize("@caseSecurity.canAccessCase(#casenumber, principal)")
    public String replaceEvidence(@RequestParam String casenumber,
                                  @RequestParam UUID groupId,
                                  @RequestParam MultipartFile file,
                                  @AuthenticationPrincipal Account user) {
        String redirect = "redirect:/caseoverview?casenumber=" + URLEncoder.encode(casenumber, StandardCharsets.UTF_8);
        try {
            if (!file.isEmpty()) {
                evidenceFileService.upload(casenumber, casenumber, "Ersatt från ärendeöversikt", file, user.getUsername(), groupId);
            }
        } catch (IOException e) {
            return redirect + "&uploadError=1";
        }
        return redirect;
    }

    @PostMapping("/caseoverview/delete")
    @PreAuthorize("@caseSecurity.canAccessCase(#casenumber, principal)")
    public String deleteEvidence(@RequestParam String casenumber,
                                 @RequestParam UUID groupId,
                                 @AuthenticationPrincipal Account user) {
        evidenceFileService.deleteGroup(groupId, user);
        return "redirect:/caseoverview?casenumber=" + URLEncoder.encode(casenumber, StandardCharsets.UTF_8);
    }

    private void prepareModel(Model model, Account user) {
        List<Cases> caseList = caseService.getAuthzCases(user.getId());
        model.addAttribute("accountoverview", user.getAuthorities().stream()
                .anyMatch(ga -> ga.getAuthority().equals("ROLE_ADMIN")));
        model.addAttribute("cases", caseService.getReportsWithCaseNumber(caseList));
        model.addAttribute("currentUser", user);
        model.addAttribute("user", user);
        model.addAttribute("isHandler", user.getAuthorities().stream()
                .anyMatch(ga -> ga.getAuthority().equals("ROLE_HANDLER")));
    }
}
