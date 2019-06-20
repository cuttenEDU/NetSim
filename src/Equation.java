import java.util.Scanner;

public class Equation {

    private static double x1;
    private static double x2;

    public static void main(String[] args) {
        double a,b,c;
        Scanner in = new Scanner(System.in);
        System.out.println("Введите a:");
        a = in.nextDouble();
        System.out.println("Введите b:");
        b = in.nextDouble();
        System.out.println("Введите c:");
        c = in.nextDouble();
        solveEq(a,b,c);;
        System.out.println("X1 = " + x1);
        System.out.println("X2 = " + x2);
    }

    static void solveEq(double a, double b, double c){
        double D = b*b - 4*a*c;
        try {
            x1 = (-b + Math.sqrt(D)) / (2 * a);
            x2 = (-b - Math.sqrt(D)) / (2 * a);
        }
        catch (Exception e){
            System.err.println("Ошибка вычисления");
            System.err.print(e.toString());
        }
    }

}
