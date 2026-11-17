package io.github.nmaksimka.bugtrack.web;

import io.github.nmaksimka.bugtrack.common.DomainException;
import io.github.nmaksimka.bugtrack.user.RegistrationService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    private final RegistrationService registrationService;

    public AuthController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("form", new RegisterForm("", "", ""));
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("form") RegisterForm form, BindingResult binding,
                           RedirectAttributes redirect) {
        if (binding.hasErrors()) {
            return "auth/register";
        }
        try {
            registrationService.register(form.name(), form.email(), form.password());
        } catch (DomainException e) {
            binding.rejectValue("email", "taken", e.getMessage());
            return "auth/register";
        }
        redirect.addFlashAttribute("success", "Аккаунт создан, войдите");
        return "redirect:/login";
    }

    public record RegisterForm(
            @NotBlank(message = "Укажите имя") @Size(max = 100, message = "Не длиннее 100 символов") String name,
            @NotBlank(message = "Укажите почту") @Email(message = "Неверный формат почты") String email,
            @Size(min = 8, max = 72, message = "Пароль от 8 до 72 символов") String password
    ) {
    }
}
