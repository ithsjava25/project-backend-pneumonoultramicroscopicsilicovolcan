package org.example.crimearchive.controllers;

import jakarta.validation.Valid;
import org.example.crimearchive.DTO.Polis.DTOUpdateProfile;
import org.example.crimearchive.cases.CaseService;
import org.example.crimearchive.cases.Cases;
import org.example.crimearchive.exceptions.PasswordValidationException;
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

import java.util.List;

@Controller
public class ProfileController {

    private final CaseService caseService;
    private final UserService userService;

    public ProfileController(CaseService caseService, UserService userService) {
        this.caseService = caseService;
        this.userService = userService;
    }


    @GetMapping("/profile")
    public String profilePage(@AuthenticationPrincipal Account user, Model model) {
        prepareModel(model, user);
        model.addAttribute("updateProfile", userService.prefillProfileFields(user));
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
    @PreAuthorize("@caseSecurity.canAccessCase(#casenumber, principal)")
    public String caseoverview(@AuthenticationPrincipal Account user,
                               Model model, @RequestParam String casenumber) {
        prepareModel(model, user);
        model.addAttribute("assignedPolice", caseService.getAllPoliceForCase(casenumber));
        model.addAttribute("rawcasenumber", casenumber);
        model.addAttribute("reportList", caseService.getReportSet(casenumber));
        return "caseoverview";
    }

    private void prepareModel(Model model, Account user) {
        List<Cases> caseList = caseService.getAuthzCases(user.getId());
        model.addAttribute("assigncasebutton", user.getAuthorities().stream()
                .anyMatch(ga -> ga.getAuthority().equals("ROLE_HANDLER")));
        model.addAttribute("accountoverview", user.getAuthorities().stream()
                .anyMatch(ga -> ga.getAuthority().equals("ROLE_ADMIN")));
        model.addAttribute("cases", caseService.getReportsWithCaseNumber(caseList));
        model.addAttribute("currentUser", user);
        model.addAttribute("user", user);
    }
}
