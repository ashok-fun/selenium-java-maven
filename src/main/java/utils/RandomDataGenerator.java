package utils;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import net.datafaker.Faker;

public final class RandomDataGenerator {
    private static final ThreadLocal<Faker> FAKER = ThreadLocal.withInitial(Faker::new);

    private RandomDataGenerator() {
    }

    public static String randomFirstName() {
        return FAKER.get().name().firstName();
    }

    public static String randomLastName() {
        return FAKER.get().name().lastName();
    }

    public static String randomEmail() {
        String generatedEmail = FAKER.get().internet().emailAddress();
        int separator = generatedEmail.indexOf('@');
        String uniqueSuffix = UUID.randomUUID().toString().replace("-", "");
        return generatedEmail.substring(0, separator) + "." + uniqueSuffix + generatedEmail.substring(separator);
    }

    public static LocalDate randomDate(LocalDate startInclusive, LocalDate endInclusive) {
        Objects.requireNonNull(startInclusive, "startInclusive must not be null");
        Objects.requireNonNull(endInclusive, "endInclusive must not be null");
        if (endInclusive.isBefore(startInclusive)) {
            throw new IllegalArgumentException("endInclusive must not be before startInclusive");
        }
        long days = ChronoUnit.DAYS.between(startInclusive, endInclusive);
        return startInclusive.plusDays(ThreadLocalRandom.current().nextLong(days + 1));
    }
}