package org.kpi.usb.controller;

import org.kpi.usb.entity.PullRequest;
import org.kpi.usb.entity.Repository;
import org.kpi.usb.entity.Result;
import org.kpi.usb.entity.Student;
import org.kpi.usb.service.GithubWebHookService;
import org.kpi.usb.service.ResultService;
import org.kpi.usb.service.TestingService;
import org.kpi.usb.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/")
public class MainController {

    private TestingService testingService;
    private UserService userService;
    private ResultService resultService;
    private GithubWebHookService githubWebHookService;

    public MainController(TestingService testingService,
                          UserService userService,
                          ResultService resultService,
                          GithubWebHookService githubWebHookService) {
        this.testingService = testingService;
        this.userService = userService;
        this.resultService = resultService;
        this.githubWebHookService = githubWebHookService;
    }

    @PostMapping
    public void receiveWebHook(@RequestBody String content) {
        Repository repository = githubWebHookService.getRepositoryFromJSON(content);
        Student student = githubWebHookService.getStudentFromJSON(content);
        PullRequest pullRequest = githubWebHookService.getPullRequestFromJSON(content);

        Optional<Integer> studentVariantOptional = userService.getUserVariantByGithubID(student.getId());
        //TODO Check optional before get :)
        int studentVariant = studentVariantOptional.get();

        final int mark = testingService.runTest(repository.getName(), student.getLogin(), studentVariant, 5);

        Result result = Result.builder()
                .studentGithubLogin(student.getLogin())
                .studentGithubID(student.getId())
                .repositoryName(repository.getName())
                .mark(mark)
                .variant(studentVariant)
                .language(repository.getLanguage())
                .updatedDate(pullRequest.getUpdatedDate())
                .build();

        resultService.sendResultToPersistenceServer(result);
    }

}

