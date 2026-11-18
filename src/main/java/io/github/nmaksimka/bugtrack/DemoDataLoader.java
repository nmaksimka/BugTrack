package io.github.nmaksimka.bugtrack;

import io.github.nmaksimka.bugtrack.bug.Bug;
import io.github.nmaksimka.bugtrack.bug.BugDescription;
import io.github.nmaksimka.bugtrack.bug.BugRepository;
import io.github.nmaksimka.bugtrack.bug.Priority;
import io.github.nmaksimka.bugtrack.bug.Severity;
import io.github.nmaksimka.bugtrack.discussion.Comment;
import io.github.nmaksimka.bugtrack.discussion.HistoryEntry;
import io.github.nmaksimka.bugtrack.fix.FixAttempt;
import io.github.nmaksimka.bugtrack.fix.FixDetails;
import io.github.nmaksimka.bugtrack.project.Membership;
import io.github.nmaksimka.bugtrack.project.Project;
import io.github.nmaksimka.bugtrack.project.ProjectRepository;
import io.github.nmaksimka.bugtrack.project.ProjectRole;
import io.github.nmaksimka.bugtrack.release.Release;
import io.github.nmaksimka.bugtrack.release.ReleaseRepository;
import io.github.nmaksimka.bugtrack.user.User;
import io.github.nmaksimka.bugtrack.user.UserRepository;
import jakarta.persistence.EntityManager;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.EnumSet;
import java.util.List;

@Component
@ConditionalOnProperty(name = "bugtrack.demo-data", havingValue = "true")
public class DemoDataLoader implements ApplicationRunner {

    static final String DEMO_PASSWORD = "password123";

    private final UserRepository users;
    private final ProjectRepository projects;
    private final BugRepository bugs;
    private final ReleaseRepository releases;
    private final EntityManager entityManager;
    private final PasswordEncoder passwordEncoder;

    public DemoDataLoader(UserRepository users, ProjectRepository projects, BugRepository bugs,
                          ReleaseRepository releases, EntityManager entityManager, PasswordEncoder passwordEncoder) {
        this.users = users;
        this.projects = projects;
        this.bugs = bugs;
        this.releases = releases;
        this.entityManager = entityManager;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (users.count() > 0) {
            return;
        }
        User maria = user("Мария Кузнецова", "maria@bugtrack.local");
        User sergey = user("Сергей Волков", "sergey@bugtrack.local");
        User ivan = user("Иван Петров", "ivan@bugtrack.local");
        User anna = user("Анна Смирнова", "anna@bugtrack.local");
        User admin = user("Администратор", "admin@bugtrack.local");
        admin.makeAdmin();

        Project portal = new Project("Веб-портал", "BT", "Клиентский веб-портал",
                "https://github.com/example/web-portal", LocalDate.now().plusDays(12));
        Membership pm = portal.addMember(maria, EnumSet.of(ProjectRole.MANAGER));
        Membership lead = portal.addMember(sergey, EnumSet.of(ProjectRole.TEAM_LEAD));
        Membership dev = portal.addMember(ivan, EnumSet.of(ProjectRole.DEVELOPER));
        Membership tester = portal.addMember(anna, EnumSet.of(ProjectRole.TESTER));
        projects.save(portal);

        Project mobile = new Project("Мобильное приложение", "MA", "Приложение для Android и iOS",
                null, LocalDate.now().plusDays(27));
        mobile.addMember(maria, EnumSet.of(ProjectRole.MANAGER));
        mobile.addMember(ivan, EnumSet.of(ProjectRole.DEVELOPER, ProjectRole.TEAM_LEAD));
        Membership mobileTester = mobile.addMember(anna, EnumSet.of(ProjectRole.TESTER));
        projects.save(mobile);

        Release current = releases.save(new Release(portal, "1.4.0", "Исправления профиля и входа",
                LocalDate.now().plusDays(7)));
        releases.save(new Release(portal, "1.4.1", "Поиск и мелкие доработки", LocalDate.now().plusDays(21)));

        Bug search = bug(portal, tester, "Не работает поиск",
                "Ввести «заказ» в поиск → нажать Enter", "Список найденных заказов", "Пустая страница",
                "Windows 11 · Firefox 132 · тестовая среда", Priority.MEDIUM, Severity.MAJOR);
        search.accept(Priority.MEDIUM, Severity.MAJOR, LocalDate.now().plusDays(10), dev);
        search.startWork(dev);
        history(search, tester, "Баг создан", null, "Новый");
        history(search, lead, "Принят и назначен", "Новый", "Назначен · Иван Петров");
        history(search, dev, "Начата работа", "Назначен", "В работе");

        Bug profile = bug(portal, tester, "Не сохраняется профиль",
                "Открыть профиль → изменить имя → сохранить", "Изменения сохранены", "Имя осталось прежним",
                "Windows 11 · Chrome 131 · тестовая среда", Priority.HIGH, Severity.MAJOR);
        profile.accept(Priority.HIGH, Severity.MAJOR, LocalDate.now().plusDays(3), dev);
        profile.startWork(dev);
        profile.submitForReview(dev);
        entityManager.persist(new FixAttempt(profile, 1, new FixDetails(
                "Исправлено сохранение профиля без телефона",
                List.of("https://github.com/example/web-portal/pull/58"), null,
                "1.4.0-rc2", "тестовый стенд · Windows 11 · Chrome",
                "Изменить имя без телефона. Сохранить и войти повторно."), dev, tester));
        current.addBug(profile, true);
        entityManager.persist(Comment.of(profile, tester, "Ошибка повторяется без номера телефона"));
        history(profile, tester, "Баг создан", null, "Новый");
        history(profile, lead, "Принят и назначен", "Новый", "Назначен · Иван Петров");
        history(profile, dev, "Начата работа", "Назначен", "В работе");
        history(profile, dev, "Передан на проверку", "В работе", "На проверке · Анна Смирнова");

        Bug login = bug(portal, tester, "Ошибка входа",
                "Ввести верный пароль с пробелом в конце почты → Войти", "Вход выполнен",
                "«Неверный логин или пароль»", "macOS 15 · Safari 18 · прод", Priority.HIGH, Severity.CRITICAL);
        history(login, tester, "Баг создан", null, "Новый");

        Bug crash = bug(mobile, mobileTester, "Падает при повороте экрана",
                "Открыть корзину → повернуть телефон", "Корзина перерисовывается", "Приложение закрывается",
                "Android 14 · Pixel 7", Priority.HIGH, Severity.CRITICAL);
        history(crash, mobileTester, "Баг создан", null, "Новый");

        releases.save(current);
        bugs.saveAll(List.of(search, profile, login, crash));
    }

    private User user(String name, String email) {
        return users.save(new User(name, email, passwordEncoder.encode(DEMO_PASSWORD)));
    }

    private Bug bug(Project project, Membership author, String title, String steps, String expected,
                    String actual, String environment, Priority priority, Severity severity) {
        Bug bug = new Bug(project, author,
                new BugDescription(title, null, steps, expected, actual, environment), priority, severity);
        return bugs.save(bug);
    }

    private void history(Bug bug, Membership author, String action, String oldValue, String newValue) {
        entityManager.persist(new HistoryEntry(bug, author, action, oldValue, newValue));
    }
}
