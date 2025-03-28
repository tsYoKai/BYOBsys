import java.util.Scanner;

public class Entrada {
 public static void main(String[] args){
Scanner sc = new Scanner(System.in);

 System.out.println("Digite sua idade");
 int idade = sc.nextInt();
sc.nextLine();
 System.out.println("Digite seu nome");
 String nome  =  sc.nextLine();

 System.out.println("Ola" + nome +"sua idade é" + idade);
 sc.close();

}
}
