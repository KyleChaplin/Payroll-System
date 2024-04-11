package application.employees;

import java.util.Random;

public class EmployeeGenerator {
    private static final String[] FIRST_NAMES = {"John", "Jane", "Michael", "Emily", "David", "Sarah", "Chris", "Emma", "James", "Olivia"};
    private static final String[] LAST_NAMES = {"Smith", "Johnson", "Williams", "Jones", "Brown", "Davis", "Miller", "Wilson", "Moore", "Taylor"};
    private static final String[] LOCATIONS = {"London", "Manchester", "Birmingham", "Glasgow", "Liverpool", "Bristol", "Edinburgh", "Leeds", "Sheffield", "Newcastle"};
    private static final String[] CONTRACT_TYPES = {"Full-time", "Part-time", "Temporary", "Contractor"};
    private static final String[] DEPARTMENTS = {"Sales", "Marketing", "Finance", "Human Resources", "Information Technology", "Operations", "Customer Service", "Research and Development"};
    private static final String[] JOB_TITLES = {"Manager", "Engineer", "Analyst", "Specialist", "Consultant", "Coordinator", "Administrator", "Supervisor", "Assistant", "Director"};

    public static String[] generateEmployeeData() {
        String[] employeeData = new String[12];
        Random random = new Random();

        employeeData[0] = FIRST_NAMES[random.nextInt(FIRST_NAMES.length)];
        employeeData[1] = LAST_NAMES[random.nextInt(LAST_NAMES.length)];
        employeeData[2] = generateEmail(employeeData[0], employeeData[1]);
        employeeData[3] = generatePhoneNumber();
        employeeData[4] = generateHourlySalary();
        employeeData[5] = generateNiNumber();
        employeeData[6] = LOCATIONS[random.nextInt(LOCATIONS.length)];
        employeeData[7] = CONTRACT_TYPES[random.nextInt(CONTRACT_TYPES.length)];
        employeeData[8] = generateContractedHours();
        employeeData[9] = DEPARTMENTS[random.nextInt(DEPARTMENTS.length)];
        employeeData[10] = JOB_TITLES[random.nextInt(JOB_TITLES.length)];
        employeeData[11] = String.valueOf(generateAccessLevel());

        return employeeData;
    }

    private static String generateEmail(String firstName, String lastName) {
        String emailPrefix = firstName.toLowerCase() + "." + lastName.toLowerCase();
        return emailPrefix + "@example.com";
    }

    private static String generatePhoneNumber() {
        StringBuilder phoneNumber = new StringBuilder("0");
        Random random = new Random();
        for (int i = 0; i < 10; i++) {
            phoneNumber.append(random.nextInt(10));
        }
        return phoneNumber.toString();
    }

    private static String generateNiNumber() {
        String alphabet = "ABCDEFGHKLMNPRSTWXYZ";
        String numbers = "0123456789";

        Random random = new Random();

        // Generate the prefix letters
        StringBuilder prefix = new StringBuilder();
        prefix.append(alphabet.charAt(random.nextInt(alphabet.length())));
        char secondLetter;
        do {
            secondLetter = alphabet.charAt(random.nextInt(alphabet.length()));
        } while (secondLetter == 'D' || secondLetter == 'F' || secondLetter == 'I' ||
                secondLetter == 'Q' || secondLetter == 'U' || secondLetter == 'V' || secondLetter == 'O');
        prefix.append(secondLetter);

        // Generate the six digits
        StringBuilder digits = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            digits.append(numbers.charAt(random.nextInt(numbers.length())));
        }

        // Generate the suffix letter
        char suffix = (char) ('A' + random.nextInt(4));

        // Construct the NI number
        return prefix.toString() + " " + digits.toString().substring(0, 2) + " " + digits.toString().substring(2, 4) + " " + digits.toString().substring(4, 6) + " " + suffix;
    }

    private static String generateHourlySalary() {
        Random random = new Random();
        double salary = random.nextDouble() * 100 + 10; // Generate between 10 and 110
        return String.format("%.2f", salary);
    }

    private static int generateAccessLevel() {
        Random random = new Random();
        return random.nextInt(4) + 1; // Generate between 1 and 4
    }

    private static String generateContractedHours() {
        Random random = new Random();
        int hours = random.nextInt(40) + 10; // Generate between 10 and 49
        return String.valueOf(hours);
    }
}
