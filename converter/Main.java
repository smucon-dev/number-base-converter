package converter;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        // main menu
        while (true) {
            System.out.print("Enter two numbers in format: {source base} {target base} (To quit type /exit) ");
            var input = scanner.nextLine().split(" ");

            if (input[0].equals("/exit")) { break; }

            if (input.length == 2 && input[0].matches("\\d+") && input[0].matches("\\d+")) {

                var sourceBase = Integer.valueOf(input[0]).intValue();
                var targetBase = Integer.valueOf(input[1]).intValue();

                if (sourceBase < 2 || targetBase < 2 || sourceBase > 36 || targetBase > 36) {
                    System.out.println("Bases must be between 2 and 36 (inclusive).");
                } else {
                    // source number sub menu
                    while (true) {
                        System.out.printf("Enter number in base %d to convert to base %d (To go back type /back) ", sourceBase, targetBase);
                        var sourceNumber = scanner.nextLine();

                        if (sourceNumber.equals("/back")) { break; }

                        try {
                            // we could make our lives easier here and use BigInteger built-in functionality,
                            // however we want to prove that we are able to perform the conversion by ourselves
                            System.out.printf("Conversion result: %s%n%n", NumberBaseConverter.convert(sourceNumber, sourceBase, targetBase));
                        } catch (NumberFormatException e) {
                            System.out.printf("Your source number does not match your source base. Please check.%n%n");
                        }
                    }
                }
            } else {
                System.out.printf("Your bases cannot be converted into numbers.%n%n");
            }

        }
    }
}
