package io.github.nmaksimka.bugtrack.web;

import io.github.nmaksimka.bugtrack.common.DomainException;
import io.github.nmaksimka.bugtrack.project.Project;
import io.github.nmaksimka.bugtrack.project.ProjectService;
import io.github.nmaksimka.bugtrack.security.CurrentUser;
import jakarta.validation.Valid;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.time.LocalDate;
import java.util.UUID;

@Controller
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/projects";
    }

    @GetMapping("/projects")
    public String list(@AuthenticationPrincipal CurrentUser user, Model model) {
        model.addAttribute("form", new ProjectForm("", "", "", "", null));
        return listPage(user, model);
    }

    @PostMapping("/projects")
    public String create(@AuthenticationPrincipal CurrentUser user,
                         @Valid @ModelAttribute("form") ProjectForm form, BindingResult binding, Model model) {
        if (binding.hasErrors()) {
            return listPage(user, model);
        }
        try {
            Project project = projectService.create(user.id(), form.name(), form.key(),
                    blankToNull(form.description()), blankToNull(form.repository()), form.deadline());
            return "redirect:/projects/" + project.getId();
        } catch (DomainException e) {
            binding.rejectValue("key", "taken", e.getMessage());
            return listPage(user, model);
        }
    }

    @GetMapping("/projects/{projectId}")
    public String open(@AuthenticationPrincipal CurrentUser user, @PathVariable UUID projectId, Model model) {
        model.addAttribute("project", projectService.details(user.id(), projectId));
        return "projects/overview";
    }

    private String listPage(CurrentUser user, Model model) {
        model.addAttribute("projects", projectService.listForUser(user.id()));
        return "projects/list";
    }

    private static String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    public record ProjectForm(
            @NotBlank(message = "Укажите название") @Size(max = 150, message = "Не длиннее 150 символов") String name,
            @Pattern(regexp = "[A-Za-z]{2,10}", message = "Ключ — от 2 до 10 латинских букв") String key,
            @Size(max = 2000, message = "Не длиннее 2000 символов") String description,
            @Pattern(regexp = "^$|https?://.+", message = "Ссылка должна начинаться с http:// или https://") String repository,
            @FutureOrPresent(message = "Срок не может быть в прошлом")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate deadline
    ) {
    }
}
