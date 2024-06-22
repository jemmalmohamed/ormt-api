package ma.org.ancfcc.pva.seeder.data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.github.javafaker.Faker;

import lombok.RequiredArgsConstructor;
import ma.org.ancfcc.pva.modules.planaction.PlanAction;
import ma.org.ancfcc.pva.modules.planaction.service.PlanActionService;

@Component
@Order(5)
@RequiredArgsConstructor
public class PlanActionSeeder implements CommandLineRunner {

    @Value("${starter.database.seed}")
    private String seeding;

    private final PlanActionService planActionService;

    Faker faker = new Faker(Locale.FRENCH);

    @Override
    public void run(String... args) throws Exception {
        if (!seeding.equals("true"))
            return;
        String[] planActions = { "2013", "2014",
                "2015", "2016", "2017", "2018", "2019", "2020", "2021", "2022", "2023", "2024" };

        List<PlanAction> planActionList = new ArrayList<>();

        for (int i = 0; i < planActions.length; i++) {

            planActionList.add(createPlanAction(planActions[i]));
        }

        planActionService.saveAll(planActionList);
    }

    PlanAction createPlanAction(String plan) {
        PlanAction planAction = new PlanAction();
        planAction.setNom(plan);
        planAction.setDescription("");
        LocalDateTime startDate = LocalDateTime.of(LocalDate.of(Integer.parseInt(plan), 1, 1), LocalTime.of(0, 0, 0));

        planAction.setDebutDate(startDate);

        LocalDateTime endDate = LocalDateTime.of(LocalDate.of(Integer.parseInt(plan), 12, 31),
                LocalTime.of(23, 59, 59));
        planAction.setFinDate(endDate);

        return planAction;
    }

}