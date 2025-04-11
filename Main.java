

public class Main {
    
    public static void main(String[] args) {
        Pessoa p1 = new Pessoa();
        p1.nome  = "Danilo";
        p1.idade = 39;
        p1.apresentar();

        ContaBancaria cb= new ContaBancaria();
        cb.setSaldo(0);
        System.out.println(cb.getSaldo());


        Calculadora calc = new Calculadora();
        calc.somar(5, 17);
        int res = calc.multiplicar(5, 6);
    }
}
