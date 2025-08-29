import java.util.InputMismatchException;
import java.util.Random;
import java.util.Scanner;

public class Project {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int choice;

        do {
            System.out.println("\nChoose an option:");
            System.out.println("1. BMI Calculator");
            System.out.println("2. Math Calculator");
            System.out.println("3. Number Guessing Game");
            System.out.println("4. Unit Converter");
            System.out.println("5. Area Calculator");
            System.out.println("6. Volume Calculator");
            System.out.println("7. Exit");
            System.out.print("Enter your choice: ");
            
            try {
                choice = scanner.nextInt();
                switch (choice) {
                    case 1:
                        bmiCalculator(scanner);
                        break;
                    case 2:
                        mathCalculator(scanner);
                        break;
                    case 3:
                        numberGuessingGame(scanner);
                        break;
                    case 4:
                        unitConverter(scanner);
                        break;
                    case 5:
                        areaCalculator(scanner);
                        break;
                    case 6:
                        volumeCalculator(scanner);
                        break;
                    case 7:
                        System.out.println("Exiting...");
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number between 1-7.");
                scanner.next();
                choice = 0;
            }
        } while (choice != 7);

        scanner.close();
    }

    private static void bmiCalculator(Scanner scanner) {
        try {
            System.out.print("Enter weight (kg): ");
            double weight = scanner.nextDouble();
            System.out.print("Enter height (m): ");
            double height = scanner.nextDouble();

            if (weight <= 0 || height <= 0) {
                throw new IllegalArgumentException("Weight and height must be positive values.");
            }

            double bmi = weight / (height * height);
            System.out.println("Your BMI: " + bmi);

            if (bmi < 18.5) {
                System.out.println("Category: Underweight");
            } else if (bmi < 24.9) {
                System.out.println("Category: Normal weight");
            } else if (bmi < 29.9) {
                System.out.println("Category: Overweight");
            } else {
                System.out.println("Category: Obesity");
            }
        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Please enter numeric values.");
            scanner.next();
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void mathCalculator(Scanner scanner) {
        try {
            System.out.println("\nChoose operation:");
            System.out.println("1. Addition (+)");
            System.out.println("2. Subtraction (-)");
            System.out.println("3. Multiplication (*)");
            System.out.println("4. Division (/)");
            System.out.println("5. Exponentiation (^)");
            System.out.println("6. Square Root (√)");
            System.out.println("7. Factorial (!)");
            System.out.println("8. Modulus (%)");
            System.out.println("9. Sine (sin)");
            System.out.println("10. Cosine (cos)");
            System.out.println("11. Tangent (tan)");
            System.out.println("12. Logarithm (log)");
            System.out.println("13. Natural Logarithm (ln)");
            System.out.println("14. Absolute Value (abs)");
            System.out.print("Enter your choice: ");
            int operation = scanner.nextInt();

            double result = 0;
            double num1, num2;
            
            switch (operation) {
                case 1:
                    num1 = getValidDouble(scanner);
                    num2 = getValidDouble(scanner);
                    result = num1 + num2;
                    break;
                case 2:
                    num1 = getValidDouble(scanner);
                    num2 = getValidDouble(scanner);
                    result = num1 - num2;
                    break;
                case 3:
                    num1 = getValidDouble(scanner);
                    num2 = getValidDouble(scanner);
                    result = num1 * num2;
                    break;
                case 4:
                    num1 = getValidDouble(scanner);
                    num2 = getValidDouble(scanner);
                    if (num2 == 0) {
                        throw new ArithmeticException("Division by zero is not allowed.");
                    }
                    result = num1 / num2;
                    break;
                case 5:
                    num1 = getValidDouble(scanner);
                    num2 = getValidDouble(scanner);
                    result = Math.pow(num1, num2);
                    break;
                case 6:
                    num1 = getValidDouble(scanner);
                    if (num1 < 0) {
                        throw new IllegalArgumentException("Square root of a negative number is not real.");
                    }
                    result = Math.sqrt(num1);
                    break;
                case 7:
                    int n = getValidNonNegativeInt(scanner);
                    result = factorial(n);
                    break;
                case 8:
                    num1 = getValidDouble(scanner);
                    num2 = getValidDouble(scanner);
                    if (num2 == 0) {
                        throw new ArithmeticException("Modulus by zero is not allowed.");
                    }
                    result = num1 % num2;
                    break;
                case 9:
                    num1 = getValidDouble(scanner);
                    result = Math.sin(Math.toRadians(num1));
                    break;
                case 10:
                    num1 = getValidDouble(scanner);
                    result = Math.cos(Math.toRadians(num1));
                    break;
                case 11:
                    num1 = getValidDouble(scanner);
                    result = Math.tan(Math.toRadians(num1));
                    break;
                case 12:
                    num1 = getValidDouble(scanner);
                    if (num1 <= 0) {
                        throw new IllegalArgumentException("Logarithm of a non-positive number is undefined.");
                    }
                    result = Math.log10(num1);
                    break;
                case 13:
                    num1 = getValidDouble(scanner);
                    if (num1 <= 0) {
                        throw new IllegalArgumentException("Natural logarithm of a non-positive number is undefined.");
                    }
                    result = Math.log(num1);
                    break;
                case 14:
                    num1 = getValidDouble(scanner);
                    result = Math.abs(num1);
                    break;
                default:
                    System.out.println("Invalid operation.");
                    return;
            }

            System.out.println("Result: " + result);
        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Please enter numeric values.");
            scanner.next();
        } catch (IllegalArgumentException | ArithmeticException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static double getValidDouble(Scanner scanner) {
        while (true) {
            try {
                return scanner.nextDouble();
            } catch (InputMismatchException e) {
                System.out.print("Invalid input. Please enter a valid number: ");
                scanner.next();
            }
        }
    }

    private static int getValidNonNegativeInt(Scanner scanner) {
        while (true) {
            try {
                int value = scanner.nextInt();
                if (value < 0) {
                    System.out.print("Please enter a non-negative integer: ");
                    continue;
                }
                return value;
            } catch (InputMismatchException e) {
                System.out.print("Invalid input. Please enter a valid integer: ");
                scanner.next();
            }
        }
    }

    private static int factorial(int n) {
        if (n == 0 || n == 1) {
            return 1;
        }
        return n * factorial(n - 1);
    }

    private static void numberGuessingGame(Scanner scanner) {
        try {
            Random random = new Random();
            int numberToGuess = random.nextInt(100) + 1; 
            int numberOfTries = 0;
            int guess;

            System.out.println("\nWelcome to the Number Guessing Game!");
            System.out.println("I have chosen a number between 1 and 100. Can you guess it?");

            do {
                System.out.print("Enter your guess (1-100): ");
                guess = scanner.nextInt();
                numberOfTries++;

                if (guess < 1 || guess > 100) {
                    System.out.println("Please enter a number between 1 and 100.");
                    continue;
                }

                if (guess < numberToGuess) {
                    System.out.println("Too low! Try again.");
                } else if (guess > numberToGuess) {
                    System.out.println("Too high! Try again.");
                } else {
                    System.out.println("Congratulations! You guessed the number in " + numberOfTries + " tries.");
                }
            } while (guess != numberToGuess);
        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Please enter a number between 1-100.");
            scanner.next();
        }
    }

    private static void unitConverter(Scanner scanner) {
        try {
            System.out.println("\nChoose conversion type:");
            System.out.println("1. Length");
            System.out.println("2. Weight");
            System.out.println("3. Temperature");
            System.out.println("4. Volume");
            System.out.println("5. Area");
            System.out.print("Enter your choice: ");
            int conversionType = scanner.nextInt();

            switch (conversionType) {
                case 1:
                    lengthConverter(scanner);
                    break;
                case 2:
                    weightConverter(scanner);
                    break;
                case 3:
                    temperatureConverter(scanner);
                    break;
                case 4:
                    volumeConverter(scanner);
                    break;
                case 5:
                    areaConverter(scanner);
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Please enter a number between 1-5.");
            scanner.next();
        }
    }

    private static void lengthConverter(Scanner scanner) {
        try {
            System.out.println("\nChoose length conversion:");
            System.out.println("1. Meters to Feet");
            System.out.println("2. Feet to Meters");
            System.out.println("3. Kilometers to Miles");
            System.out.println("4. Miles to Kilometers");
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();

            System.out.print("Enter length: ");
            double length = getValidPositiveDouble(scanner);

            switch (choice) {
                case 1:
                    System.out.println(length + " meters = " + (length * 3.28084) + " feet");
                    break;
                case 2:
                    System.out.println(length + " feet = " + (length / 3.28084) + " meters");
                    break;
                case 3:
                    System.out.println(length + " kilometers = " + (length * 0.621371) + " miles");
                    break;
                case 4:
                    System.out.println(length + " miles = " + (length / 0.621371) + " kilometers");
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Please enter numeric values.");
            scanner.next();
        }
    }

    private static double getValidPositiveDouble(Scanner scanner) {
        while (true) {
            try {
                double value = scanner.nextDouble();
                if (value <= 0) {
                    System.out.print("Please enter a positive number: ");
                    continue;
                }
                return value;
            } catch (InputMismatchException e) {
                System.out.print("Invalid input. Please enter a valid positive number: ");
                scanner.next();
            }
        }
    }

    private static void weightConverter(Scanner scanner) {
        try {
            System.out.println("\nChoose weight conversion:");
            System.out.println("1. Kilograms to Pounds");
            System.out.println("2. Pounds to Kilograms");
            System.out.println("3. Grams to Ounces");
            System.out.println("4. Ounces to Grams");
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();

            System.out.print("Enter weight: ");
            double weight = getValidPositiveDouble(scanner);

            switch (choice) {
                case 1:
                    System.out.println(weight + " kg = " + (weight * 2.20462) + " lbs");
                    break;
                case 2:
                    System.out.println(weight + " lbs = " + (weight / 2.20462) + " kg");
                    break;
                case 3:
                    System.out.println(weight + " grams = " + (weight * 0.035274) + " ounces");
                    break;
                case 4:
                    System.out.println(weight + " ounces = " + (weight / 0.035274) + " grams");
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Please enter numeric values.");
            scanner.next();
        }
    }

    private static void temperatureConverter(Scanner scanner) {
        try {
            System.out.println("\nChoose temperature conversion:");
            System.out.println("1. Celsius to Fahrenheit");
            System.out.println("2. Fahrenheit to Celsius");
            System.out.println("3. Celsius to Kelvin");
            System.out.println("4. Kelvin to Celsius");
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();

            System.out.print("Enter temperature: ");
            double temp = getValidDouble(scanner);

            switch (choice) {
                case 1:
                    System.out.println(temp + " °C = " + ((temp * 9 / 5) + 32) + " °F");
                    break;
                case 2:
                    System.out.println(temp + " °F = " + ((temp - 32) * 5 / 9) + " °C");
                    break;
                case 3:
                    System.out.println(temp + " °C = " + (temp + 273.15) + " K");
                    break;
                case 4:
                    System.out.println(temp + " K = " + (temp - 273.15) + " °C");
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Please enter numeric values.");
            scanner.next();
        }
    }

    private static void volumeConverter(Scanner scanner) {
        try {
            System.out.println("\nChoose volume conversion:");
            System.out.println("1. Liters to Gallons");
            System.out.println("2. Gallons to Liters");
            System.out.println("3. Milliliters to Fluid Ounces");
            System.out.println("4. Fluid Ounces to Milliliters");
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();

            System.out.print("Enter volume: ");
            double volume = getValidPositiveDouble(scanner);

            switch (choice) {
                case 1:
                    System.out.println(volume + " liters = " + (volume * 0.264172) + " gallons");
                    break;
                case 2:
                    System.out.println(volume + " gallons = " + (volume / 0.264172) + " liters");
                    break;
                case 3:
                    System.out.println(volume + " milliliters = " + (volume * 0.033814) + " fluid ounces");
                    break;
                case 4:
                    System.out.println(volume + " fluid ounces = " + (volume / 0.033814) + " milliliters");
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Please enter numeric values.");
            scanner.next();
        }
    }

    private static void areaConverter(Scanner scanner) {
        try {
            System.out.println("\nChoose area conversion:");
            System.out.println("1. Square Meters to Square Feet");
            System.out.println("2. Square Feet to Square Meters");
            System.out.println("3. Hectares to Acres");
            System.out.println("4. Acres to Hectares");
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();

            System.out.print("Enter area: ");
            double area = getValidPositiveDouble(scanner);

            switch (choice) {
                case 1:
                    System.out.println(area + " square meters = " + (area * 10.7639) + " square feet");
                    break;
                case 2:
                    System.out.println(area + " square feet = " + (area / 10.7639) + " square meters");
                    break;
                case 3:
                    System.out.println(area + " hectares = " + (area * 2.47105) + " acres");
                    break;
                case 4:
                    System.out.println(area + " acres = " + (area / 2.47105) + " hectares");
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Please enter numeric values.");
            scanner.next();
        }
    }

    private static void areaCalculator(Scanner scanner) {
        try {
            System.out.println("\nChoose a shape to calculate the area:");
            System.out.println("1. Circle");
            System.out.println("2. Rectangle");
            System.out.println("3. Triangle");
            System.out.println("4. Square");
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    System.out.print("Enter the radius of the circle: ");
                    double radius = getValidPositiveDouble(scanner);
                    System.out.println("Area of Circle: " + (Math.PI * radius * radius));
                    break;
                case 2:
                    System.out.print("Enter the length of the rectangle: ");
                    double length = getValidPositiveDouble(scanner);
                    System.out.print("Enter the width of the rectangle: ");
                    double width = getValidPositiveDouble(scanner);
                    System.out.println("Area of Rectangle: " + (length * width));
                    break;
                case 3:
                    System.out.print("Enter the base of the triangle: ");
                    double base = getValidPositiveDouble(scanner);
                    System.out.print("Enter the height of the triangle: ");
                    double height = getValidPositiveDouble(scanner);
                    System.out.println("Area of Triangle: " + (0.5 * base * height));
                    break;
                case 4:
                    System.out.print("Enter the side length of the square: ");
                    double side = getValidPositiveDouble(scanner);
                    System.out.println("Area of Square: " + (side * side));
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Please enter numeric values.");
            scanner.next();
        }
    }

    private static void volumeCalculator(Scanner scanner) {
        try {
            System.out.println("\nChoose a shape to calculate the volume:");
            System.out.println("1. Sphere");
            System.out.println("2. Cube");
            System.out.println("3. Cylinder");
            System.out.println("4. Cuboid");
            System.out.println("5. Cone");
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    System.out.print("Enter the radius of the sphere: ");
                    double radius = getValidPositiveDouble(scanner);
                    System.out.println("Volume of Sphere: " + ((4.0 / 3.0) * Math.PI * Math.pow(radius, 3)));
                    break;
                case 2:
                    System.out.print("Enter the side length of the cube: ");
                    double side = getValidPositiveDouble(scanner);
                    System.out.println("Volume of Cube: " + Math.pow(side, 3));
                    break;
                case 3:
                    System.out.print("Enter the radius of the cylinder: ");
                    double cylinderRadius = getValidPositiveDouble(scanner);
                    System.out.print("Enter the height of the cylinder: ");
                    double cylinderHeight = getValidPositiveDouble(scanner);
                    System.out.println("Volume of Cylinder: " + (Math.PI * Math.pow(cylinderRadius, 2) * cylinderHeight));
                    break;
                case 4:
                    System.out.print("Enter the length of the cuboid: ");
                    double length = getValidPositiveDouble(scanner);
                    System.out.print("Enter the width of the cuboid: ");
                    double width = getValidPositiveDouble(scanner);
                    System.out.print("Enter the height of the cuboid: ");
                    double height = getValidPositiveDouble(scanner);
                    System.out.println("Volume of Cuboid: " + (length * width * height));
                    break;
                case 5:
                    System.out.print("Enter the radius of the cone: ");
                    double coneRadius = getValidPositiveDouble(scanner);
                    System.out.print("Enter the height of the cone: ");
                    double coneHeight = getValidPositiveDouble(scanner);
                    System.out.println("Volume of Cone: " + ((1.0 / 3.0) * Math.PI * Math.pow(coneRadius, 2) * coneHeight));
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Please enter numeric values.");
            scanner.next();
        }
    }
}